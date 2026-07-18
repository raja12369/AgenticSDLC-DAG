package com.exam.agenticsdlc.orchestrator;

import com.exam.agenticsdlc.agents.AgentExecutor;
import com.exam.agenticsdlc.agents.AgentRegistry;
import com.exam.agenticsdlc.specs.BaseSpec;
import com.exam.agenticsdlc.workflow.ExecutionTraceEntry;
import com.exam.agenticsdlc.workflow.WorkflowContext;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.*;

/**
 * Executes the SDLC phase graph (see PhaseGraph) with real governance:
 *
 *  - non-linear, stateful execution driven by an explicit dependency graph
 *    (not a hardcoded enum iteration order)
 *  - phases within the same topological "wave" that don't depend on each
 *    other (e.g. TEST + DOCUMENTATION) run concurrently, then join
 *  - human approval checkpoints: phases flagged in PhaseGraph.requiresApproval
 *    will not execute until WorkflowContext.isApproved(phase) is true -
 *    the run stops (safe-stop) at that point rather than proceeding
 *  - bounded retry is handled at the agent level via SmallRye Fault
 *    Tolerance (@Retry on each agent's run method); this layer additionally
 *    treats an agent failure as a safe-stop for everything downstream of it
 *    rather than continuing to build on top of a failed phase
 *  - every phase attempt is recorded to WorkflowContext's audit trail with
 *    status/timing, giving the basis for success-rate / MTTR / latency metrics
 */
@ApplicationScoped
public class GraphOrchestrator {

    private static final Logger LOG = Logger.getLogger(GraphOrchestrator.class);

    @Inject AgentRegistry registry;
    @Inject AgentExecutor executor;
    @Inject SpecBackfillEngine backfill;

    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    public enum StopReason { COMPLETED, AWAITING_APPROVAL, FAILED }

    public static class RunResult {
        public StopReason stopReason;
        public SpecType stoppedAt;
        public String message;
    }

    /** Run every phase reachable from the current context state, stopping at approval gates or failures. */
    public RunResult runAll(WorkflowContext ctx, String userInput) {
        for (Set<SpecType> wave : PhaseGraph.executionWaves()) {
            List<SpecType> toRun = new ArrayList<>();
            for (SpecType type : wave) {
                if (isDone(ctx, type)) continue;
                toRun.add(type);
            }
            if (toRun.isEmpty()) continue;

            // Approval gate check happens before dispatch - safe-stop, don't execute.
            for (SpecType type : toRun) {
                if (PhaseGraph.requiresApproval(type) && !ctx.isApproved(type.name())) {
                    ctx.addTraceEntry(gateEntry(type, "AWAITING_APPROVAL"));
                    RunResult r = new RunResult();
                    r.stopReason = StopReason.AWAITING_APPROVAL;
                    r.stoppedAt = type;
                    r.message = "Phase " + type + " requires human approval. POST /sdlc/" + ctx.getRunId()
                            + "/approve/" + type.name().toLowerCase() + " then resume.";
                    return r;
                }
            }

            // Dispatch this wave concurrently (parallel-eligible phases), join before next wave.
            List<Future<PhaseOutcome>> futures = new ArrayList<>();
            for (SpecType type : toRun) {
                futures.add(pool.submit(() -> runPhase(ctx, type, userInput)));
            }
            for (Future<PhaseOutcome> f : futures) {
                PhaseOutcome outcome;
                try {
                    outcome = f.get();
                } catch (Exception e) {
                    RunResult r = new RunResult();
                    r.stopReason = StopReason.FAILED;
                    r.message = "Executor error: " + e.getMessage();
                    return r;
                }
                if (!outcome.success) {
                    // Safe-stop: do not proceed to phases depending on a failed phase.
                    RunResult r = new RunResult();
                    r.stopReason = StopReason.FAILED;
                    r.stoppedAt = outcome.type;
                    r.message = "Phase " + outcome.type + " failed: " + outcome.error;
                    return r;
                }
            }
        }

        RunResult r = new RunResult();
        r.stopReason = StopReason.COMPLETED;
        return r;
    }

    /** Run exactly one phase (used by the single-phase API), enforcing its graph dependencies + approval gate. */
    public BaseSpec runSinglePhase(WorkflowContext ctx, SpecType type, String userInput) {
        Set<SpecType> missing = new HashSet<>();
        for (SpecType dep : PhaseGraph.dependenciesOf(type)) {
            if (!isDone(ctx, dep)) missing.add(dep);
        }
        if (!missing.isEmpty()) {
            ctx.addTraceEntry(gateEntry(type, "BLOCKED"));
            throw new IllegalStateException("Phase " + type + " is blocked - missing completed dependencies: " + missing
                    + ". Run those phases first (context is preserved across calls for this runId).");
        }
        if (PhaseGraph.requiresApproval(type) && !ctx.isApproved(type.name())) {
            ctx.addTraceEntry(gateEntry(type, "AWAITING_APPROVAL"));
            throw new IllegalStateException("Phase " + type + " requires human approval before it can run. "
                    + "POST /sdlc/" + ctx.getRunId() + "/approve/" + type.name().toLowerCase() + " first.");
        }

        PhaseOutcome outcome = runPhase(ctx, type, userInput);
        if (!outcome.success) {
            throw new RuntimeException("Phase " + type + " failed: " + outcome.error);
        }
        return outcome.output;
    }

    // ------------------------------------------------------------------

    private static class PhaseOutcome {
        SpecType type;
        boolean success;
        BaseSpec output;
        String error;
    }

    private PhaseOutcome runPhase(WorkflowContext ctx, SpecType type, String userInput) {
        PhaseOutcome outcome = new PhaseOutcome();
        outcome.type = type;

        Class<? extends BaseSpec> inputType = registry.getInputType(type);
        String contextKey = registry.getInputContextKey(type);

        ExecutionTraceEntry trace = ExecutionTraceEntry.started(type.name(), agentSimpleName(type), 1);
        ctx.addTraceEntry(trace);

        try {
            BaseSpec inputSpec = backfill.ensureSpec(ctx, type, contextKey, userInput, inputType);
            Object agent = registry.getAgent(type);
            BaseSpec output = executor.execute(agent, inputSpec, ctx);

            ctx.put(type.name().toLowerCase() + "Spec", output);
            trace.complete(true, null);

            outcome.success = true;
            outcome.output = output;
        } catch (Exception e) {
            LOG.errorf(e, "Phase %s failed", type);
            String msg = rootMessage(e);
            trace.complete(false, msg);
            outcome.success = false;
            outcome.error = msg;
        }
        return outcome;
    }

    private static String rootMessage(Throwable t) {
        Throwable current = t;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        String msg = current.getMessage();
        return msg == null ? current.getClass().getSimpleName() : msg;
    }

    private boolean isDone(WorkflowContext ctx, SpecType type) {
        return ctx.has(type.name().toLowerCase() + "Spec");
    }

    private ExecutionTraceEntry gateEntry(SpecType type, String status) {
        ExecutionTraceEntry e = ExecutionTraceEntry.started(type.name(), agentSimpleName(type), 0);
        e.setStatus(status);
        e.setEndTime(e.getStartTime());
        return e;
    }

    private String agentSimpleName(SpecType type) {
        return type.name().charAt(0) + type.name().substring(1).toLowerCase() + "Agent";
    }
}
