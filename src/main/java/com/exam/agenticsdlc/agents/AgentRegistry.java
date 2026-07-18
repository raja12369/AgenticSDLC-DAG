package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.orchestrator.SpecType;
import com.exam.agenticsdlc.specs.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.EnumMap;
import java.util.Map;

@ApplicationScoped
public class AgentRegistry {

    private final Map<SpecType, Object> registry = new EnumMap<>(SpecType.class);

    // 🔥 NEW: simple map of phase → input spec type
    private final Map<SpecType, Class<? extends BaseSpec>> inputTypes = Map.of(
            SpecType.REQUIREMENT, RequirementSpec.class,
            SpecType.TASK, RequirementSpec.class,
            SpecType.IMPACT, TaskSpec.class,
            SpecType.DESIGN, ImpactAnalysisSpec.class,
            SpecType.CODE, DesignSpec.class,
            SpecType.TEST, CodeSpec.class,
            SpecType.DOCUMENTATION, CodeSpec.class,
            SpecType.RELEASE, DocumentationSpec.class,
            SpecType.SUMMARY, ReleaseSpec.class
    );

    @Inject RequirementAgent requirementAgent;
    @Inject TaskDecompositionAgent taskAgent;
    @Inject ImpactAnalysisAgent impactAgent;
    @Inject DesignAgent designAgent;
    @Inject CodeAgent codeAgent;
    @Inject TestAgent testAgent;
    @Inject DocumentationAgent documentationAgent;
    @Inject ReleaseAgent releaseAgent;
    @Inject SummaryAgent summaryAgent;

    public AgentRegistry() {}

    @Inject
    void init() {
        registry.put(SpecType.REQUIREMENT, requirementAgent);
        registry.put(SpecType.TASK, taskAgent);
        registry.put(SpecType.IMPACT, impactAgent);
        registry.put(SpecType.DESIGN, designAgent);
        registry.put(SpecType.CODE, codeAgent);
        registry.put(SpecType.TEST, testAgent);
        registry.put(SpecType.DOCUMENTATION, documentationAgent);
        registry.put(SpecType.RELEASE, releaseAgent);
        registry.put(SpecType.SUMMARY, summaryAgent);
    }

    public Object getAgent(SpecType type) {
        return registry.get(type);
    }

    // 🔥 NEW: orchestrator calls this to get correct input type
    public Class<? extends BaseSpec> getInputType(SpecType type) {
        return inputTypes.get(type);
    }

    public String getInputContextKey(SpecType type) {
        Class<? extends BaseSpec> inputType = inputTypes.get(type);
        return inputType.getSimpleName().substring(0,1).toLowerCase()
                + inputType.getSimpleName().substring(1);
    }
}
