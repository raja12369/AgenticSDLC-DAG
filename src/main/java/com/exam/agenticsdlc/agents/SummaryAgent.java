package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.llm.ModelFactory;
import com.exam.agenticsdlc.llm.ModelName;
import com.exam.agenticsdlc.llm.ProviderType;
import com.exam.agenticsdlc.specs.ReleaseSpec;
import com.exam.agenticsdlc.specs.SummarySpec;
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
public class SummaryAgent {

    private static final Logger LOG = Logger.getLogger(SummaryAgent.class);

    @Inject
    ModelFactory modelFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(120000)
    public SummarySpec run(WorkflowContext ctx, ReleaseSpec release) {

        ChatLanguageModel model =
                modelFactory.get(ProviderType.OLLAMA, ModelName.REQUIREMENT);

        String prompt = """
                You are an SDLC summarization agent.

                Produce a SummarySpec as JSON with EXACTLY this shape:

                {
                  "notes": "a full narrative summary of the entire SDLC run: what was built, key decisions, risks, and outcome",
                  "normalizedText": "one paragraph summary"
                }

                Release Phase Summary to summarize:
                %s

                Respond ONLY with valid JSON matching the shape above. No markdown, no explanation, no code fences.
                """.formatted(release.getNormalizedText());

        String llmResponse = model.generate(prompt);

        String json = LLMJsonUtils.extractJson(llmResponse);

        try {
            SummarySpec spec = mapper.readValue(json, SummarySpec.class);

            spec.setPrompt(prompt);
            spec.setResponseJson(json);
            spec.setAgentName("SummaryAgent");
            spec.setTimestamp(System.currentTimeMillis());
            spec.setSynthetic(false);
            spec.setSkipped(false);

            ctx.put("summarySpec", spec);
            return spec;

        } catch (Exception e) {
            LOG.warnf(e, "SummaryAgent: LLM response did not parse as SummarySpec, degrading to synthetic. Raw: %s", json);
            SummarySpec fallback = new SummarySpec();
            fallback.setRaw(json);
            fallback.setResponseJson(json);
            fallback.setPrompt(prompt);
            fallback.setAgentName("SummaryAgent");
            fallback.setTimestamp(System.currentTimeMillis());
            fallback.setSynthetic(true);
            fallback.setSkipped(false);
            fallback.setNormalizedText("Fallback: LLM output did not match SummarySpec schema. See 'raw' field.");

            ctx.put("summarySpec", fallback);
            return fallback;
        }
    }
}
