package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.llm.ModelFactory;
import com.exam.agenticsdlc.llm.ModelName;
import com.exam.agenticsdlc.llm.ProviderType;
import com.exam.agenticsdlc.specs.CodeSpec;
import com.exam.agenticsdlc.specs.DesignSpec;
import com.exam.agenticsdlc.util.LLMJsonUtils;
import com.exam.agenticsdlc.workflow.WorkflowContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

@ApplicationScoped
public class CodeAgent {

    @Inject
    ModelFactory modelFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(120000)
    public CodeSpec run(WorkflowContext ctx, DesignSpec design) {

        ChatLanguageModel model =
                modelFactory.get(ProviderType.OLLAMA, ModelName.CODE);

        String prompt = """
                You are a senior software engineer.

                Based on the following design, produce a CodeSpec as JSON with EXACTLY this shape
                (field names must match exactly - do not rename, nest, or omit them):

                {
                  "files": { "path/to/File1.ext": "full file contents as a string", "path/to/File2.ext": "..." },
                  "language": "e.g. Java",
                  "framework": "e.g. Quarkus",
                  "buildInstructions": "how to build/run this code",
                  "lintWarnings": ["any notable warnings, or empty array"],
                  "normalizedText": "one paragraph summary of what was implemented"
                }

                "files" is REQUIRED and must contain at least one real, non-empty file with actual code -
                it is a flat map of file path to file content, not a nested object and not a single string.

                Design to implement:
                %s

                Respond ONLY with valid JSON matching the shape above. No markdown, no explanation, no code fences.
                """.formatted(design.getNormalizedText());

        String llmResponse = model.generate(prompt);

        String json = LLMJsonUtils.extractJson(llmResponse);

        try {
            CodeSpec spec = mapper.readValue(json, CodeSpec.class);

            spec.setPrompt(prompt);
            spec.setResponseJson(json);
            spec.setAgentName("CodeAgent");
            spec.setTimestamp(System.currentTimeMillis());
            spec.setSynthetic(false);
            spec.setSkipped(false);

            ctx.put("codeSpec", spec);
            return spec;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CodeSpec JSON: " + json, e);
        }
    }
}

