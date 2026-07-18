package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.llm.ModelFactory;
import com.exam.agenticsdlc.llm.ModelName;
import com.exam.agenticsdlc.llm.ProviderType;
import com.exam.agenticsdlc.specs.DesignSpec;
import com.exam.agenticsdlc.specs.ImpactAnalysisSpec;
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
public class DesignAgent {

    private static final Logger LOG = Logger.getLogger(DesignAgent.class);

    @Inject
    ModelFactory modelFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(120000)
    public DesignSpec run(WorkflowContext ctx, ImpactAnalysisSpec impact) {

        ChatLanguageModel model =
                modelFactory.get(ProviderType.OLLAMA, ModelName.DESIGN);

        String prompt = """
                You are a senior software architect.

                Produce a DesignSpec as JSON with EXACTLY this shape:

                {
                  "architectureOverview": "narrative description of the architecture",
                  "components": ["component 1", "component 2"],
                  "dataFlow": ["step 1", "step 2"],
                  "apiDefinitions": ["METHOD /path - description"],
                  "databaseSchema": ["table/entity descriptions"],
                  "nonFunctionalRequirements": ["e.g. must handle 1000 req/s"],
                  "risks": ["risk 1"],
                  "openQuestions": ["question 1"],
                  "normalizedText": "one paragraph summary of the design"
                }

                Based on the following impact analysis:
                %s

                Respond ONLY with valid JSON matching the shape above. No markdown, no explanation, no code fences.
                """.formatted(impact.getNormalizedText());

        String llmResponse = model.generate(prompt);

        String json = LLMJsonUtils.extractJson(llmResponse);

        try {
            DesignSpec spec = mapper.readValue(json, DesignSpec.class);

            spec.setPrompt(prompt);
            spec.setResponseJson(json);
            spec.setAgentName("DesignAgent");
            spec.setTimestamp(System.currentTimeMillis());
            spec.setSynthetic(false);
            spec.setSkipped(false);

            ctx.put("designSpec", spec);
            return spec;

        } catch (Exception e) {
            LOG.warnf(e, "DesignAgent: LLM response did not parse as DesignSpec, degrading to synthetic. Raw: %s", json);
            DesignSpec fallback = new DesignSpec();
            fallback.setRaw(json);
            fallback.setResponseJson(json);
            fallback.setPrompt(prompt);
            fallback.setAgentName("DesignAgent");
            fallback.setTimestamp(System.currentTimeMillis());
            fallback.setSynthetic(true);
            fallback.setSkipped(false);
            fallback.setNormalizedText("Fallback: LLM output did not match DesignSpec schema. See 'raw' field.");

            ctx.put("designSpec", fallback);
            return fallback;
        }
    }
}
