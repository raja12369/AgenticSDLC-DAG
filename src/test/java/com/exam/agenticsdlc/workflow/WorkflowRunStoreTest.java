package com.exam.agenticsdlc.workflow;

import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkflowRunStoreTest {

    @Test
    void createReturnsAContextRetrievableByRunId() {
        WorkflowRunStore store = new WorkflowRunStore();
        WorkflowContext created = store.create();

        WorkflowContext fetched = store.get(created.getRunId());

        assertSame(created, fetched, "Fetching by runId should return the SAME context instance, "
                + "proving state genuinely persists across separate calls");
    }

    @Test
    void unknownRunIdThrowsNotFound() {
        WorkflowRunStore store = new WorkflowRunStore();
        assertThrows(NotFoundException.class, () -> store.get("does-not-exist"));
    }

    @Test
    void dataPutInOneCallIsVisibleInAnother() {
        WorkflowRunStore store = new WorkflowRunStore();
        WorkflowContext ctx = store.create();
        ctx.put("requirementSpec", "fake-spec-object");

        WorkflowContext sameRunLaterCall = store.get(ctx.getRunId());

        assertEquals("fake-spec-object", sameRunLaterCall.get("requirementSpec"));
    }

    @Test
    void approvalIsPerPhaseAndPersists() {
        WorkflowContext ctx = new WorkflowContext();
        assertFalse(ctx.isApproved("RELEASE"));

        ctx.approve("RELEASE");

        assertTrue(ctx.isApproved("RELEASE"));
        assertFalse(ctx.isApproved("SUMMARY"), "Approval should be scoped to the specific phase");
    }

    @Test
    void traceEntriesAccumulateAcrossPhases() {
        WorkflowContext ctx = new WorkflowContext();
        ExecutionTraceEntry e1 = ExecutionTraceEntry.started("REQUIREMENT", "RequirementAgent", 1);
        e1.complete(true, null);
        ctx.addTraceEntry(e1);

        ExecutionTraceEntry e2 = ExecutionTraceEntry.started("TASK", "TaskDecompositionAgent", 1);
        e2.complete(false, "LLM returned malformed JSON");
        ctx.addTraceEntry(e2);

        assertEquals(2, ctx.getTrace().size());
        assertEquals("SUCCEEDED", ctx.getTrace().get(0).getStatus());
        assertEquals("FAILED", ctx.getTrace().get(1).getStatus());
    }
}
