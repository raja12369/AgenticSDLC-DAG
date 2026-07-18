package com.exam.agenticsdlc.orchestrator;

public enum PhaseStatus {
    PENDING,             // not yet attempted
    BLOCKED,              // dependencies not satisfied
    AWAITING_APPROVAL,    // dependencies satisfied but human approval required and not yet given
    RUNNING,
    DONE,
    FAILED
}
