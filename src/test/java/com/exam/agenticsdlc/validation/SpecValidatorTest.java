package com.exam.agenticsdlc.validation;

import com.exam.agenticsdlc.gates.ValidationGate;
import com.exam.agenticsdlc.specs.RequirementSpec;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpecValidatorTest {

    private final SpecValidator validator = new SpecValidator();
    private final ValidationGate gate = new ValidationGate();

    @Test
    void nonSyntheticSpecWithoutAcceptanceCriteriaFailsValidation() {
        RequirementSpec spec = new RequirementSpec();
        spec.setSynthetic(false);
        spec.setAgentName("RequirementAgent");
        spec.setResponseJson("{}");
        spec.setAcceptanceCriteria(List.of()); // empty - should be a hard failure

        ValidationResult result = validator.validate(spec);

        assertFalse(result.isValid(), "Empty acceptance criteria on a real (non-synthetic) spec should fail validation");
        assertFalse(result.getErrors().isEmpty());
        assertFalse(gate.allow(result), "Gate should block a spec that failed validation");
    }

    @Test
    void syntheticSpecIsAllowedThroughAsWarningOnly() {
        RequirementSpec spec = new RequirementSpec();
        spec.setSynthetic(true);
        spec.setAcceptanceCriteria(List.of());

        ValidationResult result = validator.validate(spec);

        assertTrue(result.isValid(), "Synthetic/backfilled specs should warn, not hard-fail");
        assertTrue(gate.allow(result));
    }

    @Test
    void wellFormedNonSyntheticSpecPassesValidation() {
        RequirementSpec spec = new RequirementSpec();
        spec.setSynthetic(false);
        spec.setAgentName("RequirementAgent");
        spec.setResponseJson("{\"normalizedText\":\"...\"}");
        spec.setNormalizedText("Build a login feature");
        spec.setAcceptanceCriteria(List.of("User can log in with valid credentials"));

        ValidationResult result = validator.validate(spec);

        assertTrue(result.isValid());
        assertTrue(gate.allow(result));
    }

    @Test
    void nullSpecFailsValidation() {
        ValidationResult result = validator.validate(null);
        assertFalse(result.isValid());
        assertFalse(gate.allow(result));
    }
}
