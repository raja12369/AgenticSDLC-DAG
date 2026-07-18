package com.exam.agenticsdlc.agents;

import com.exam.agenticsdlc.specs.*;
import com.exam.agenticsdlc.validation.SpecValidator;
import com.exam.agenticsdlc.validation.ValidationResult;
import com.exam.agenticsdlc.gates.ValidationGate;
import com.exam.agenticsdlc.workflow.WorkflowContext;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgentExecutor {

    private final SpecValidator validator = new SpecValidator();
    private final ValidationGate gate = new ValidationGate();

    public BaseSpec execute(Object agent, BaseSpec input, WorkflowContext ctx) {

        try {
            BaseSpec output = null;

            if (agent instanceof RequirementAgent a) {
                output = a.run(ctx, (RequirementSpec) input);
            }
            else if (agent instanceof TaskDecompositionAgent a) {
                output = a.run(ctx, (RequirementSpec) input);
            }
            else if (agent instanceof ImpactAnalysisAgent a) {
                output = a.run(ctx, (TaskSpec) input);
            }
            else if (agent instanceof DesignAgent a) {
                output = a.run(ctx, (ImpactAnalysisSpec) input);
            }
            else if (agent instanceof CodeAgent a) {
                output = a.run(ctx, (DesignSpec) input);
            }
            else if (agent instanceof TestAgent a) {
                output = a.run(ctx, (CodeSpec) input);
            }
            else if (agent instanceof DocumentationAgent a) {
                output = a.run(ctx, (CodeSpec) input);
            }
            else if (agent instanceof ReleaseAgent a) {
                output = a.run(ctx, (DocumentationSpec) input);
            }
            else if (agent instanceof SummaryAgent a) {
                output = a.run(ctx, (ReleaseSpec) input);
            }
            else {
                throw new IllegalArgumentException("Unknown agent type: " + agent.getClass());
            }

            // Validate output
            ValidationResult result = validator.validate(output);
            if (!gate.allow(result)) {
                throw new IllegalStateException("Validation failed: " + result.getErrors());
            }

            return output;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Agent execution failed: " + agent.getClass().getSimpleName() + " - " + rootMessage(e), e);
        }
    }

    /** Walks the cause chain to find the deepest (most specific) exception message. */
    private static String rootMessage(Throwable t) {
        Throwable current = t;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        String msg = current.getMessage();
        return msg == null ? current.getClass().getSimpleName() : msg;
    }
}
