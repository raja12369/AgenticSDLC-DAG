package com.exam.agenticsdlc.workflow;

/**
 * Audit-grade record of a single phase execution.
 * Captures the data needed to compute reliability metrics:
 * success rate, retry frequency, MTTR, end-to-end latency, decision lineage.
 */
public class ExecutionTraceEntry {

    private String phase;
    private String agentName;
    private String status;       // STARTED | SUCCEEDED | FAILED | RETRIED | BLOCKED | AWAITING_APPROVAL
    private long startTime;
    private long endTime;
    private long durationMs;
    private int attempt;
    private String errorMessage;
    private String note;

    public ExecutionTraceEntry() {}

    public static ExecutionTraceEntry started(String phase, String agentName, int attempt) {
        ExecutionTraceEntry e = new ExecutionTraceEntry();
        e.phase = phase;
        e.agentName = agentName;
        e.attempt = attempt;
        e.status = "STARTED";
        e.startTime = System.currentTimeMillis();
        return e;
    }

    public void complete(boolean success, String errorMessage) {
        this.endTime = System.currentTimeMillis();
        this.durationMs = this.endTime - this.startTime;
        this.status = success ? "SUCCEEDED" : "FAILED";
        this.errorMessage = errorMessage;
    }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }

    public int getAttempt() { return attempt; }
    public void setAttempt(int attempt) { this.attempt = attempt; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
