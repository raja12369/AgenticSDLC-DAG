package com.exam.agenticsdlc.util;

import com.exam.agenticsdlc.specs.RequirementSpec;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class LooseJsonMapper {

    public static RequirementSpec toRequirementSpec(Map<String, Object> raw, String prompt, String json) {
        RequirementSpec spec = new RequirementSpec();

        spec.setNormalizedText((String) raw.getOrDefault("normalizedText", ""));
        spec.setSynthetic((Boolean) raw.getOrDefault("synthetic", true));
        spec.setAssumptions((List<String>) raw.getOrDefault("assumptions", List.of()));
        spec.setAcceptanceCriteria((List<String>) raw.getOrDefault("acceptanceCriteria", List.of()));
        spec.setConstraints((List<String>) raw.getOrDefault("constraints", List.of()));
        spec.setAmbiguities((List<String>) raw.getOrDefault("ambiguities", List.of()));
        spec.setMissingInfo((List<String>) raw.getOrDefault("missingInfo", List.of()));
        spec.setSkipped((Boolean) raw.getOrDefault("skipped", false));

        spec.setAgentName("RequirementAgent");
        spec.setPrompt(prompt);
        spec.setResponseJson(json);
        spec.setTimestamp(System.currentTimeMillis());
        spec.setVersion("1.0");

        return spec;
    }
}
