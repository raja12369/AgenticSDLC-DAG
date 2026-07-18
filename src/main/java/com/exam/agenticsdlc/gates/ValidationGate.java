package com.exam.agenticsdlc.gates;

import com.exam.agenticsdlc.validation.ValidationResult;

public class ValidationGate {

    public boolean allow(ValidationResult result) {
        // In a stricter system, you might block on errors:
        return result.isValid();
    }
}
