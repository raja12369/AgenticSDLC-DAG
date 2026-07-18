package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.llm.ModelFactory;
import com.exam.agenticsdlc.llm.ModelName;
import com.exam.agenticsdlc.llm.ProviderType;
import com.exam.agenticsdlc.specs.CodeSpec;
import com.exam.agenticsdlc.specs.TestSpec;
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
public class TestAgent {

    private static final Logger LOG = Logger.getLogger(TestAgent.class);

    @Inject
    ModelFactory modelFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(120000)
    public TestSpec run(WorkflowContext ctx, CodeSpec code) {

        ChatLanguageModel model =
                modelFactory.get(ProviderType.OLLAMA, ModelName.TEST);

        String prompt = """
                You are a senior QA automation engineer.

                Produce a TestSpec as JSON with EXACTLY this shape:

                {
                  "unitTests": [ { "name": "test name", "description": "what it checks",
                    "input": "example input", "expectedOutput": "expected result" } ],
                  "integrationTests": [ { "name": "test name", "description": "what it checks",
                    "input": "example input", "expectedOutput": "expected result" } ],
                  "coverageEstimate": 80,
                  "missingTests": ["gaps not covered"],
                  "normalizedText": "one paragraph summary of the test plan"
                }

                "coverageEstimate" is an integer percentage (0-100), not a string.

                Based on the following code specification:
                %s

                Respond ONLY with valid JSON matching the shape above. No markdown, no explanation, no code fences.
                """.formatted(code.getNormalizedText());

        String llmResponse = model.generate(prompt);

        String json = LLMJsonUtils.extractJson(llmResponse);

        try {
            TestSpec spec = mapper.readValue(json, TestSpec.class);

            spec.setPrompt(prompt);
            spec.setResponseJson(json);
            spec.setAgentName("TestAgent");
            spec.setTimestamp(System.currentTimeMillis());
            spec.setSynthetic(false);
            spec.setSkipped(false);

            ctx.put("testSpec", spec);
            return spec;

        } catch (Exception e) {
            LOG.warnf(e, "TestAgent: LLM response did not parse as TestSpec, degrading to synthetic. Raw: %s", json);
            TestSpec fallback = new TestSpec();
            fallback.setRaw(json);
            fallback.setResponseJson(json);
            fallback.setPrompt(prompt);
            fallback.setAgentName("TestAgent");
            fallback.setTimestamp(System.currentTimeMillis());
            fallback.setSynthetic(true);
            fallback.setSkipped(false);
            fallback.setNormalizedText("Fallback: LLM output did not match TestSpec schema. See 'raw' field.");

            ctx.put("testSpec", fallback);
            return fallback;
        }
    }
}
