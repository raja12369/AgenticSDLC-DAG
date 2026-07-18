package com.exam.agenticsdlc.orchestrator;

import com.exam.agenticsdlc.specs.BaseSpec;
import com.exam.agenticsdlc.workflow.WorkflowContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SpecBackfillEngine {

    @Inject
    SpecBackfillRegistry registry;

    public BaseSpec ensureSpec(
            WorkflowContext ctx,
            SpecType type,
            String contextKey,
            String userInput,
            Class<? extends BaseSpec> inputType
    ) {
        BaseSpec spec = ctx.get(contextKey);
        if (spec != null) {
            return spec;
        }

        // 🔥 create synthetic spec based on the agent’s input type
        BaseSpec synthetic = registry.createSyntheticSpec(inputType, userInput);

        ctx.put(contextKey, synthetic);
        return synthetic;
    }


}
