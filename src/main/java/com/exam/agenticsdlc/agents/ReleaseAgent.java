package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.llm.ModelFactory;
import com.exam.agenticsdlc.llm.ModelName;
import com.exam.agenticsdlc.llm.ProviderType;
import com.exam.agenticsdlc.specs.DocumentationSpec;
import com.exam.agenticsdlc.specs.ReleaseSpec;
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
public class ReleaseAgent {

    private static final Logger LOG = Logger.getLogger(ReleaseAgent.class);

    @Inject
    ModelFactory modelFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(120000)
    public ReleaseSpec run(WorkflowContext ctx, DocumentationSpec docs) {

        ChatLanguageModel model =
                modelFactory.get(ProviderType.OLLAMA, ModelName.REQUIREMENT);

        String prompt = """
                You are a senior DevOps release engineer.

                Generate a ReleaseSpec as JSON with EXACTLY this shape:

                {
                  "releasePlan": "narrative release plan",
                  "environments": ["staging", "production"],
                  "rollbackPlan": "how to roll back if this release fails",
                  "deploymentSteps": ["step 1", "step 2"],
                  "risks": ["risk 1"],
                  "normalizedText": "one paragraph summary"
                }

                Based on the following documentation:

                %s

                Respond ONLY with valid JSON matching the shape above. No markdown, no explanation, no code fences.
                """.formatted(docs.getNormalizedText());

        String llmResponse = model.generate(prompt);

        String json = LLMJsonUtils.extractJson(llmResponse);

        try {
            ReleaseSpec spec = mapper.readValue(json, ReleaseSpec.class);

            spec.setPrompt(prompt);
            spec.setResponseJson(json);
            spec.setAgentName("ReleaseAgent");
            spec.setTimestamp(System.currentTimeMillis());
            spec.setSynthetic(false);
            spec.setSkipped(false);

            ctx.put("releaseSpec", spec);
            return spec;

        } catch (Exception e) {
            LOG.warnf(e, "ReleaseAgent: LLM response did not parse as ReleaseSpec, degrading to synthetic. Raw: %s", json);
            ReleaseSpec fallback = new ReleaseSpec();
            fallback.setRaw(json);
            fallback.setResponseJson(json);
            fallback.setPrompt(prompt);
            fallback.setAgentName("ReleaseAgent");
            fallback.setTimestamp(System.currentTimeMillis());
            fallback.setSynthetic(true);
            fallback.setSkipped(false);
            fallback.setNormalizedText("Fallback: LLM output did not match ReleaseSpec schema. See 'raw' field.");

            ctx.put("releaseSpec", fallback);
            return fallback;
        }
    }
}
