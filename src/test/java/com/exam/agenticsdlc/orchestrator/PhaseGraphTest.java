package com.exam.agenticsdlc.orchestrator;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PhaseGraphTest {

    @Test
    void requirementHasNoDependencies() {
        assertTrue(PhaseGraph.dependenciesOf(SpecType.REQUIREMENT).isEmpty());
    }

    @Test
    void releaseIsAJoinPointRequiringBothTestAndDocumentation() {
        Set<SpecType> deps = PhaseGraph.dependenciesOf(SpecType.RELEASE);
        assertEquals(EnumSet.of(SpecType.TEST, SpecType.DOCUMENTATION), deps);
    }

    @Test
    void testAndDocumentationAreIndependentAndParallelEligible() {
        // Neither should depend on the other.
        assertFalse(PhaseGraph.dependenciesOf(SpecType.TEST).contains(SpecType.DOCUMENTATION));
        assertFalse(PhaseGraph.dependenciesOf(SpecType.DOCUMENTATION).contains(SpecType.TEST));

        // They should land in the same execution wave (proving parallel eligibility).
        List<Set<SpecType>> waves = PhaseGraph.executionWaves();
        Set<SpecType> waveWithTest = waves.stream().filter(w -> w.contains(SpecType.TEST)).findFirst().orElseThrow();
        assertTrue(waveWithTest.contains(SpecType.DOCUMENTATION),
                "TEST and DOCUMENTATION should be in the same wave since neither depends on the other");
    }

    @Test
    void releaseRequiresApprovalButRequirementDoesNot() {
        assertTrue(PhaseGraph.requiresApproval(SpecType.RELEASE));
        assertFalse(PhaseGraph.requiresApproval(SpecType.REQUIREMENT));
    }

    @Test
    void isReadyReflectsCompletedSet() {
        assertFalse(PhaseGraph.isReady(SpecType.RELEASE, EnumSet.of(SpecType.TEST)));
        assertTrue(PhaseGraph.isReady(SpecType.RELEASE, EnumSet.of(SpecType.TEST, SpecType.DOCUMENTATION)));
    }

    @Test
    void topologicalOrderPlacesDependenciesBeforeDependents() {
        List<SpecType> order = PhaseGraph.topologicalOrder();
        assertTrue(order.indexOf(SpecType.REQUIREMENT) < order.indexOf(SpecType.TASK));
        assertTrue(order.indexOf(SpecType.CODE) < order.indexOf(SpecType.TEST));
        assertTrue(order.indexOf(SpecType.CODE) < order.indexOf(SpecType.DOCUMENTATION));
        assertTrue(order.indexOf(SpecType.TEST) < order.indexOf(SpecType.RELEASE));
        assertTrue(order.indexOf(SpecType.DOCUMENTATION) < order.indexOf(SpecType.RELEASE));
        assertTrue(order.indexOf(SpecType.RELEASE) < order.indexOf(SpecType.SUMMARY));
        assertEquals(SpecType.values().length, order.size());
    }
}
