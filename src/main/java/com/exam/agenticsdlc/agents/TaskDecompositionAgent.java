package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.llm.ModelFactory;
import com.exam.agenticsdlc.llm.ModelName;
import com.exam.agenticsdlc.llm.ProviderType;
import com.exam.agenticsdlc.specs.RequirementSpec;
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
public class TaskDecompositionAgent {

    private static final Logger LOG = Logger.getLogger(TaskDecompositionAgent.class);

    @Inject
    ModelFactory modelFactory;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(120000)
    public TaskSpec run(WorkflowContext ctx, RequirementSpec requirement) {

        ChatLanguageModel model =
                modelFactory.get(ProviderType.OLLAMA, ModelName.REQUIREMENT);

        String prompt = """
                You are a senior SDLC task decomposition agent.

                Convert the following requirement into a TaskSpec as JSON with EXACTLY this shape
                (field names must match exactly):

                {
                  "tasks": [
                    { "id": "t1", "name": "short task name", "description": "what this task does",
                      "type": "e.g. backend/frontend/infra/test", "dependencies": ["t0"],
                      "parentTaskId": null, "inputs": [], "outputs": [] }
                  ],
                  "dependencies": [ { "from": "t1", "to": "t2" } ],
                  "normalizedText": "one paragraph summary of the task breakdown"
                }

                "tasks" is REQUIRED and must contain at least one task with a real id and name.

                Requirement to decompose:
                %s

                Respond ONLY with valid JSON matching the shape above. No markdown, no explanation, no code fences.
                """.formatted(requirement.getNormalizedText());

        String llmResponse = model.generate(prompt);

        String json = LLMJsonUtils.extractJson(llmResponse);

        try {
            TaskSpec spec = mapper.readValue(json, TaskSpec.class);

            spec.setPrompt(prompt);
            spec.setResponseJson(json);
            spec.setAgentName("TaskDecompositionAgent");
            spec.setTimestamp(System.currentTimeMillis());
            spec.setSynthetic(false);
            spec.setSkipped(false);

            ctx.put("taskSpec", spec);
            return spec;

        } catch (Exception e) {
            // The LLM's JSON didn't bind to TaskSpec's shape. Rather than crash the whole phase,
            // degrade to a clearly-marked synthetic result carrying the raw response for
            // debugging - SpecValidator only hard-fails NON-synthetic specs, so this is allowed
            // through as a (visible, warned-about) degraded result instead of blocking the run.
            LOG.warnf(e, "TaskDecompositionAgent: LLM response did not parse as TaskSpec, degrading to synthetic. Raw: %s", json);
            TaskSpec fallback = new TaskSpec();
            fallback.setRaw(json);
            fallback.setResponseJson(json);
            fallback.setPrompt(prompt);
            fallback.setAgentName("TaskDecompositionAgent");
            fallback.setTimestamp(System.currentTimeMillis());
            fallback.setSynthetic(true);
            fallback.setSkipped(false);
            fallback.setNormalizedText("Fallback: LLM output did not match TaskSpec schema. See 'raw' field.");

            ctx.put("taskSpec", fallback);
            return fallback;
        }
    }
}
