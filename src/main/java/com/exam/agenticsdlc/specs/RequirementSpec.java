package com.exam.agenticsdlc.specs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequirementSpec extends BaseSpec {
    // All fields (ambiguities, assumptions, constraints, missingInfo,
    // acceptanceCriteria) are already declared in BaseSpec — do not
    // redeclare them here, it creates shadow fields instead of overrides.
}
