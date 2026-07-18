package com.exam.agenticsdlc.orchestrator;

import com.exam.agenticsdlc.factory.AgentResponseFactory;
import com.exam.agenticsdlc.specs.*;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

@ApplicationScoped
public class SpecBackfillRegistry {

    private final Map<SpecType, Function<String, BaseSpec>> registry = new EnumMap<>(SpecType.class);

    public SpecBackfillRegistry() {
        registry.put(SpecType.REQUIREMENT, AgentResponseFactory::syntheticRequirementSpec);
        registry.put(SpecType.TASK, AgentResponseFactory::syntheticTaskSpec);
        registry.put(SpecType.IMPACT, AgentResponseFactory::syntheticImpactAnalysisSpec);
        registry.put(SpecType.DESIGN, AgentResponseFactory::syntheticDesignSpec);
        registry.put(SpecType.CODE, AgentResponseFactory::syntheticCodeSpec);
        registry.put(SpecType.TEST, AgentResponseFactory::syntheticTestSpec);
        registry.put(SpecType.DOCUMENTATION, AgentResponseFactory::syntheticDocumentationSpec);
        registry.put(SpecType.RELEASE, AgentResponseFactory::syntheticReleaseSpec);
        registry.put(SpecType.SUMMARY, AgentResponseFactory::syntheticSummarySpec);
    }

    public BaseSpec createSyntheticSpec(Class<? extends BaseSpec> inputType, String userInput) {
        if (inputType.equals(RequirementSpec.class)) return AgentResponseFactory.syntheticRequirementSpec(userInput);
        if (inputType.equals(TaskSpec.class)) return AgentResponseFactory.syntheticTaskSpec(userInput);
        if (inputType.equals(ImpactAnalysisSpec.class)) return AgentResponseFactory.syntheticImpactAnalysisSpec(userInput);
        if (inputType.equals(DesignSpec.class)) return AgentResponseFactory.syntheticDesignSpec(userInput);
        if (inputType.equals(CodeSpec.class)) return AgentResponseFactory.syntheticCodeSpec(userInput);
        if (inputType.equals(TestSpec.class)) return AgentResponseFactory.syntheticTestSpec(userInput);
        if (inputType.equals(DocumentationSpec.class)) return AgentResponseFactory.syntheticDocumentationSpec(userInput);
        if (inputType.equals(ReleaseSpec.class)) return AgentResponseFactory.syntheticReleaseSpec(userInput);
        if (inputType.equals(SummarySpec.class)) return AgentResponseFactory.syntheticSummarySpec(userInput);

        throw new IllegalArgumentException("Unknown spec type: " + inputType);
    }



}
