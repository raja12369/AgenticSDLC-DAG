package com.exam.agenticsdlc.agents;
import com.exam.agenticsdlc.llm.ModelFactory;
import com.exam.agenticsdlc.llm.ModelName;
import com.exam.agenticsdlc.llm.ProviderType;
import com.exam.agenticsdlc.specs.ImpactAnalysisSpec;
import com.exam.agenticsdlc.specs.TaskSpec;
import com.exam.agenticsdlc.util.LLMJsonUtils;
import com.exam.agenticsdlc.workflow.WorkflowContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ImpactAnalysisAgent {

    private static final Logger LOG = Logger.getLogger(ImpactAnalysisAgent.class);

    @Inject
    ModelFactory modelFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(120000)
    public ImpactAnalysisSpec run(WorkflowContext ctx, TaskSpec tasks) {

        ChatLanguageModel model =
                modelFactory.get(ProviderType.OLLAMA, ModelName.REQUIREMENT);

        String prompt = """
                You are an SDLC impact analysis agent.

                Analyze the following tasks and produce an ImpactAnalysisSpec as JSON with
                EXACTLY this shape:

                {
                  "impactedModules": ["module or component names"],
                  "impactedServices": ["service names"],
                  "impactedAPIs": ["API endpoints affected"],
                  "impactedDataFlows": ["data flows affected"],
                  "risks": ["risk 1", "risk 2"],
                  "normalizedText": "one paragraph summary of the impact"
                }

                Tasks to analyze:
                %s

                Respond ONLY with valid JSON matching the shape above. No markdown, no explanation, no code fences.
                """.formatted(tasks.getNormalizedText());

        String llmResponse = model.generate(prompt);

        String json = LLMJsonUtils.extractJson(llmResponse);

        try {
            ImpactAnalysisSpec spec = mapper.readValue(json, ImpactAnalysisSpec.class);

            spec.setPrompt(prompt);
            spec.setResponseJson(json);
            spec.setAgentName("ImpactAnalysisAgent");
            spec.setTimestamp(System.currentTimeMillis());
            spec.setSynthetic(false);
            spec.setSkipped(false);

            ctx.put("impactSpec", spec);
            return spec;

        } catch (Exception e) {
            LOG.warnf(e, "ImpactAnalysisAgent: LLM response did not parse as ImpactAnalysisSpec, degrading to synthetic. Raw: %s", json);
            ImpactAnalysisSpec fallback = new ImpactAnalysisSpec();
            fallback.setRaw(json);
            fallback.setResponseJson(json);
            fallback.setPrompt(prompt);
            fallback.setAgentName("ImpactAnalysisAgent");
            fallback.setTimestamp(System.currentTimeMillis());
            fallback.setSynthetic(true);
            fallback.setSkipped(false);
            fallback.setNormalizedText("Fallback: LLM output did not match ImpactAnalysisSpec schema. See 'raw' field.");

            ctx.put("impactSpec", fallback);
            return fallback;
        }
    }
}
