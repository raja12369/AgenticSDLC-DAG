package com.exam.agenticsdlc.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Holds all state for a single agentic SDLC run: per-phase outputs (data),
 * the audit trail (trace), and human-approval decisions (approvals).
 *
 * One instance per run, persisted server-side by WorkflowRunStore and
 * looked up by runId across separate HTTP calls so single-phase execution
 * is genuinely stateful instead of starting from a blank context each time.
 */
public class WorkflowContext {

    private final String runId;
    private final Map<String, Object> data = new HashMap<>();
    private final List<ExecutionTraceEntry> trace = new CopyOnWriteArrayList<>();
    private final Set<String> approvedPhases = Collections.synchronizedSet(new HashSet<>());
    private final long createdAt = System.currentTimeMillis();

    public WorkflowContext() {
        this(java.util.UUID.randomUUID().toString());
    }

    public WorkflowContext(String runId) {
        this.runId = runId;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public boolean has(String key) {
        return data.containsKey(key);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getRunId() {
        return runId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void addTraceEntry(ExecutionTraceEntry entry) {
        trace.add(entry);
    }

    public List<ExecutionTraceEntry> getTrace() {
        return trace;
    }

    public void approve(String phase) {
        approvedPhases.add(phase.toUpperCase());
    }

    public boolean isApproved(String phase) {
        return approvedPhases.contains(phase.toUpperCase());
    }

    public Set<String> getApprovedPhases() {
        return approvedPhases;
    }
}
