package com.exam.agenticsdlc.api;

import com.exam.agenticsdlc.orchestrator.GraphOrchestrator;
import com.exam.agenticsdlc.orchestrator.SpecType;
import com.exam.agenticsdlc.specs.BaseSpec;
import com.exam.agenticsdlc.workflow.WorkflowContext;
import com.exam.agenticsdlc.workflow.WorkflowRunStore;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/sdlc")
@Produces(MediaType.APPLICATION_JSON)
public class SdlcResource {

    @Inject WorkflowRunStore runStore;
    @Inject GraphOrchestrator orchestrator;

    // ---------------------------------------------------------
    // 1. START A RUN (stateful - returns a runId used by every
    //    subsequent call so context is preserved across requests)
    // ---------------------------------------------------------
    @POST
    @Path("/start")
    public Response start() {
        WorkflowContext ctx = runStore.create();
        return Response.ok(Map.of("runId", ctx.getRunId())).build();
    }

    // ---------------------------------------------------------
    // 2. FULL SDLC WORKFLOW - runs the whole dependency graph,
    //    wave by wave, stopping safely at approval gates or failures
    // ---------------------------------------------------------
    @POST
    @Path("/full")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response runFullSdlc(String userInput) {
        WorkflowContext ctx = runStore.create();

        GraphOrchestrator.RunResult result = orchestrator.runAll(ctx, userInput);

        return Response.ok(Map.of(
                "runId", ctx.getRunId(),
                "stopReason", result.stopReason,
                "stoppedAt", result.stoppedAt == null ? "" : result.stoppedAt.name(),
                "message", result.message == null ? "" : result.message,
                "results", ctx.getData(),
                "trace", ctx.getTrace()
        )).build();
    }

    /** Resume a run that previously stopped at an approval gate or partway through. */
    @POST
    @Path("/{runId}/resume")
    public Response resume(@PathParam("runId") String runId) {
        WorkflowContext ctx = runStore.get(runId);
        GraphOrchestrator.RunResult result = orchestrator.runAll(ctx, null);

        return Response.ok(Map.of(
                "runId", ctx.getRunId(),
                "stopReason", result.stopReason,
                "stoppedAt", result.stoppedAt == null ? "" : result.stoppedAt.name(),
                "message", result.message == null ? "" : result.message,
                "results", ctx.getData(),
                "trace", ctx.getTrace()
        )).build();
    }

    // ---------------------------------------------------------
    // 3. SINGLE PHASE, ON AN EXISTING RUN - context is shared
    //    across calls, so calling TASK after REQUIREMENT actually
    //    builds on the real REQUIREMENT output, not a synthetic one
    // ---------------------------------------------------------
    @POST
    @Path("/{runId}/phase/{phase}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response runSinglePhase(@PathParam("runId") String runId,
                                    @PathParam("phase") String phase,
                                    String userInput) {
        WorkflowContext ctx = runStore.get(runId);
        SpecType type = SpecType.valueOf(phase.toUpperCase());

        try {
            BaseSpec result = orchestrator.runSinglePhase(ctx, type, userInput);
            return Response.ok(result).build();
        } catch (IllegalStateException blocked) {
            // Dependency not satisfied or approval pending - not a server error, a governance stop.
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", blocked.getMessage())).build();
        }
    }

    // ---------------------------------------------------------
    // 4. HUMAN APPROVAL CHECKPOINT for high-impact phases (e.g. RELEASE)
    // ---------------------------------------------------------
    @POST
    @Path("/{runId}/approve/{phase}")
    public Response approve(@PathParam("runId") String runId, @PathParam("phase") String phase) {
        WorkflowContext ctx = runStore.get(runId);
        SpecType type = SpecType.valueOf(phase.toUpperCase());
        ctx.approve(type.name());
        return Response.ok(Map.of("runId", runId, "approved", type.name())).build();
    }

    // ---------------------------------------------------------
    // 5. STATUS / AUDIT TRAIL for a run
    // ---------------------------------------------------------
    @GET
    @Path("/{runId}/status")
    public Response status(@PathParam("runId") String runId) {
        WorkflowContext ctx = runStore.get(runId);
        return Response.ok(Map.of(
                "runId", ctx.getRunId(),
                "createdAt", ctx.getCreatedAt(),
                "completedPhases", ctx.getData().keySet(),
                "approvedPhases", ctx.getApprovedPhases(),
                "trace", ctx.getTrace()
        )).build();
    }
}
