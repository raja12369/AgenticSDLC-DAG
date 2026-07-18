package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.llm.ModelFactory;
import com.exam.agenticsdlc.llm.ModelName;
import com.exam.agenticsdlc.llm.ProviderType;
import com.exam.agenticsdlc.specs.CodeSpec;
import com.exam.agenticsdlc.specs.DocumentationSpec;
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
public class DocumentationAgent {

    private static final Logger LOG = Logger.getLogger(DocumentationAgent.class);

    @Inject
    ModelFactory modelFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(120000)
    public DocumentationSpec run(WorkflowContext ctx, CodeSpec code) {

        ChatLanguageModel model =
                modelFactory.get(ProviderType.OLLAMA, ModelName.DOCUMENTATION);

        String prompt = """
                You are a senior technical writer.

                Produce a DocumentationSpec as JSON with EXACTLY this shape:

                {
                  "readme": "full README content as a single string",
                  "apiDocs": ["endpoint documentation entries"],
                  "architectureDocs": ["architecture notes"],
                  "adrs": ["architecture decision records"],
                  "usageExamples": ["example usage snippets"],
                  "normalizedText": "one paragraph summary of the documentation produced"
                }

                Based on the following code specification:
                %s

                Respond ONLY with valid JSON matching the shape above. No markdown, no explanation, no code fences.
                """.formatted(code.getNormalizedText());

        String llmResponse = model.generate(prompt);

        String json = LLMJsonUtils.extractJson(llmResponse);

        try {
            DocumentationSpec spec = mapper.readValue(json, DocumentationSpec.class);

            spec.setPrompt(prompt);
            spec.setResponseJson(json);
            spec.setAgentName("DocumentationAgent");
            spec.setTimestamp(System.currentTimeMillis());
            spec.setSynthetic(false);
            spec.setSkipped(false);

            ctx.put("documentationSpec", spec);
            return spec;

        } catch (Exception e) {
            LOG.warnf(e, "DocumentationAgent: LLM response did not parse as DocumentationSpec, degrading to synthetic. Raw: %s", json);
            DocumentationSpec fallback = new DocumentationSpec();
            fallback.setRaw(json);
            fallback.setResponseJson(json);
            fallback.setPrompt(prompt);
            fallback.setAgentName("DocumentationAgent");
            fallback.setTimestamp(System.currentTimeMillis());
            fallback.setSynthetic(true);
            fallback.setSkipped(false);
            fallback.setNormalizedText("Fallback: LLM output did not match DocumentationSpec schema. See 'raw' field.");

            ctx.put("documentationSpec", fallback);
            return fallback;
        }
    }
}
