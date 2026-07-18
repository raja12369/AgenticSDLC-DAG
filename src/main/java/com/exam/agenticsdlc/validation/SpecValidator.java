package com.exam.agenticsdlc.validation;

import com.exam.agenticsdlc.specs.*;

import java.util.ArrayList;
import java.util.List;

public class SpecValidator {

    public ValidationResult validate(BaseSpec spec) {
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (spec == null) {
            errors.add("Agent returned a null spec.");
            return new ValidationResult(false, warnings, errors);
        }

        if (spec.getNormalizedText() == null || spec.getNormalizedText().isBlank()) {
            warnings.add("normalizedText is empty.");
        }

        if (spec.isSkipped()) {
            warnings.add("Phase was skipped by user.");
        }

        // Hard failure conditions - a genuinely broken/empty non-synthetic
        // output should block the gate rather than silently propagate
        // downstream, since every later phase builds on this one.
        if (!spec.isSynthetic()) {
            if (spec.getAgentName() == null || spec.getAgentName().isBlank()) {
                errors.add("Non-synthetic spec is missing agentName - agent output is malformed.");
            }
            if (spec.getResponseJson() == null || spec.getResponseJson().isBlank()) {
                errors.add("Non-synthetic spec has no responseJson - nothing was actually produced by the LLM.");
            }
        } else {
            warnings.add("Spec is synthetic (auto-generated fallback, not real agent output).");
        }

        if (spec instanceof RequirementSpec req) {
            if (!req.isSynthetic() && (req.getAcceptanceCriteria() == null || req.getAcceptanceCriteria().isEmpty())) {
                errors.add("RequirementSpec has no acceptance criteria - requirement is not actionable.");
            }
        }

        if (spec instanceof CodeSpec code) {
            if (!code.isSynthetic() && (code.getFiles() == null || code.getFiles().isEmpty())) {
                errors.add("CodeSpec produced no files - nothing to test, document, or release.");
            }
        }

        if (spec instanceof DesignSpec design) {
            if (!design.isSynthetic() && (design.getArchitectureOverview() == null || design.getArchitectureOverview().isBlank())) {
                warnings.add("DesignSpec has no architectureOverview.");
            }
        }

        return new ValidationResult(errors.isEmpty(), warnings, errors);
    }
}
