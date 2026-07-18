package com.exam.agenticsdlc.workflow;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry of active workflow runs, keyed by runId.
 *
 * This is what makes single-phase execution genuinely stateful: instead of
 * each HTTP call constructing a throwaway WorkflowContext, callers create a
 * run once (POST /sdlc/start), get a runId back, and every subsequent call
 * against that runId shares the same context - so phase N+1 actually sees
 * phase N's real output rather than a synthetic backfilled stand-in.
 *
 * In-memory only (lost on restart) - acceptable for a prototype; swap for a
 * persistent store (DB/Redis) for production use, noted as a limitation.
 */
@ApplicationScoped
public class WorkflowRunStore {

    private final Map<String, WorkflowContext> runs = new ConcurrentHashMap<>();

    public WorkflowContext create() {
        WorkflowContext ctx = new WorkflowContext();
        runs.put(ctx.getRunId(), ctx);
        return ctx;
    }

    public WorkflowContext get(String runId) {
        WorkflowContext ctx = runs.get(runId);
        if (ctx == null) {
            throw new NotFoundException("No workflow run found for runId: " + runId);
        }
        return ctx;
    }

    public boolean exists(String runId) {
        return runs.containsKey(runId);
    }

    public Map<String, WorkflowContext> all() {
        return runs;
    }
}
