package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.llm.ModelFactory;
import com.exam.agenticsdlc.llm.ModelName;
import com.exam.agenticsdlc.llm.ProviderType;
import com.exam.agenticsdlc.specs.RequirementSpec;
import com.exam.agenticsdlc.util.LLMJsonUtils;
import com.exam.agenticsdlc.util.LooseJsonMapper;
import com.exam.agenticsdlc.workflow.WorkflowContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.util.Map;

@ApplicationScoped
public class RequirementAgent {

    @Inject
    ModelFactory modelFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(120000)
    public RequirementSpec run(WorkflowContext ctx, RequirementSpec userInput) {

        ChatLanguageModel model =
                modelFactory.get(ProviderType.OLLAMA, ModelName.REQUIREMENT);

        // EXACT RequirementSpec schema
        String schema = """
        {
          "normalizedText": "",
          "synthetic": true,
          "assumptions": [],
          "responseJson": "",
          "ambiguities": [],
          "skipped": false,
          "missingInfo": [],
          "timestamp": "",
          "version": "1.0",
          "prompt": "",
          "agentName": "RequirementAgent",
          "constraints": [],
          "acceptanceCriteria": []
        }
        """;

        String inputprompt = """
                Return ONLY valid JSON. No explanation, no markdown, no text outside JSON.
                
                 You are a senior requirements analyst.

                 Analyze the following requirement and populate ALL fields of RequirementSpec:

                 Requirement:
                 %s

                 Respond ONLY with valid JSON containing these fields:

                 {
                   "normalizedText": "",
                   "synthetic": true,
                   "assumptions": [],
                   "ambiguities": [],
                   "missingInfo": [],
                   "constraints": [],
                   "acceptanceCriteria": []
                 }
                
                """.formatted(userInput.getNormalizedText());

        String prompt = LLMJsonUtils.strictJsonPrompt(schema, inputprompt);

        try {
            String llmResponse = model.generate(prompt);

            String json = LLMJsonUtils.extractJson(llmResponse);

            Map<String, Object> raw = mapper.readValue(json, Map.class);

            return LooseJsonMapper.toRequirementSpec(raw, prompt, json);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse RequirementSpec JSON: " + e.getMessage(), e);
        }
    }
}
