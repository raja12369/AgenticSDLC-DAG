package com.exam.agenticsdlc.orchestrator;

import java.util.*;

/**
 * Explicit dependency graph for the SDLC workflow.
 *
 * This replaces the implicit "enum declaration order = execution order"
 * assumption in the old linear orchestrator. Phases are graph nodes;
 * dependencies are edges. This is a real DAG, not a chain:
 *
 *                REQUIREMENT
 *                     |
 *                   TASK
 *                     |
 *                  IMPACT
 *                     |
 *                  DESIGN
 *                     |
 *                   CODE
 *                    / \
 *                TEST   DOCUMENTATION      <-- fork (parallel-eligible)
 *                    \ /
 *                 RELEASE                  <-- join (needs BOTH)
 *                     |
 *                  SUMMARY
 *
 * RELEASE is a join point requiring both TEST and DOCUMENTATION to be
 * complete - a phase that previously (in the linear model) only ever waited
 * on DOCUMENTATION, silently skipping the test-completion dependency.
 *
 * RELEASE is also flagged as a human-approval gate: high-impact phases
 * should not execute autonomously without an explicit approval signal.
 */
public class PhaseGraph {

    private static final Map<SpecType, Set<SpecType>> DEPENDENCIES = new EnumMap<>(SpecType.class);
    private static final Set<SpecType> APPROVAL_GATED = EnumSet.of(SpecType.RELEASE);

    static {
        DEPENDENCIES.put(SpecType.REQUIREMENT, Set.of());
        DEPENDENCIES.put(SpecType.TASK, Set.of(SpecType.REQUIREMENT));
        DEPENDENCIES.put(SpecType.IMPACT, Set.of(SpecType.TASK));
        DEPENDENCIES.put(SpecType.DESIGN, Set.of(SpecType.IMPACT));
        DEPENDENCIES.put(SpecType.CODE, Set.of(SpecType.DESIGN));
        DEPENDENCIES.put(SpecType.TEST, Set.of(SpecType.CODE));
        DEPENDENCIES.put(SpecType.DOCUMENTATION, Set.of(SpecType.CODE));
        DEPENDENCIES.put(SpecType.RELEASE, Set.of(SpecType.TEST, SpecType.DOCUMENTATION));
        DEPENDENCIES.put(SpecType.SUMMARY, Set.of(SpecType.RELEASE));
    }

    public static Set<SpecType> dependenciesOf(SpecType type) {
        return DEPENDENCIES.getOrDefault(type, Set.of());
    }

    public static boolean requiresApproval(SpecType type) {
        return APPROVAL_GATED.contains(type);
    }

    /** True if `type` has no outstanding dependency not yet present in `completed`. */
    public static boolean isReady(SpecType type, Set<SpecType> completed) {
        return completed.containsAll(dependenciesOf(type));
    }

    /**
     * Topological execution order, grouped into "waves" - each wave is a
     * set of phases whose dependencies are all satisfied by prior waves,
     * and which therefore have no dependency on each other and are
     * parallel-eligible within the wave (e.g. TEST + DOCUMENTATION).
     */
    public static List<Set<SpecType>> executionWaves() {
        List<Set<SpecType>> waves = new ArrayList<>();
        Set<SpecType> completed = EnumSet.noneOf(SpecType.class);
        Set<SpecType> remaining = EnumSet.allOf(SpecType.class);

        while (!remaining.isEmpty()) {
            Set<SpecType> wave = EnumSet.noneOf(SpecType.class);
            for (SpecType type : remaining) {
                if (isReady(type, completed)) {
                    wave.add(type);
                }
            }
            if (wave.isEmpty()) {
                throw new IllegalStateException("Cycle detected in phase graph, remaining: " + remaining);
            }
            waves.add(wave);
            completed.addAll(wave);
            remaining.removeAll(wave);
        }
        return waves;
    }

    /** Flat topological order (one phase at a time) - useful for single-phase-at-a-time driving. */
    public static List<SpecType> topologicalOrder() {
        List<SpecType> order = new ArrayList<>();
        executionWaves().forEach(order::addAll);
        return order;
    }
}
