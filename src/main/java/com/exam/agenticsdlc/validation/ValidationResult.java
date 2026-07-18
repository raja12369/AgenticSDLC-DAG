package com.exam.agenticsdlc.validation;

import java.util.List;

public class ValidationResult {

    private final boolean valid;
    private final List<String> warnings;
    private final List<String> errors;

    public ValidationResult(boolean valid, List<String> warnings, List<String> errors) {
        this.valid = valid;
        this.warnings = warnings;
        this.errors = errors;
    }

    public boolean isValid() { return valid; }
    public List<String> getWarnings() { return warnings; }
    public List<String> getErrors() { return errors; }

    public static ValidationResult ok() {
        return new ValidationResult(true, List.of(), List.of());
    }

    public static ValidationResult withWarnings(List<String> warnings) {
        return new ValidationResult(true, warnings, List.of());
    }

    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, List.of(), errors);
    }
}
