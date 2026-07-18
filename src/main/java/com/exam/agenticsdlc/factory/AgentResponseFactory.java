package com.exam.agenticsdlc.factory;

import com.exam.agenticsdlc.specs.*;

import java.util.List;
import java.util.Map;

public class AgentResponseFactory {

    private static final String FACTORY_AGENT_NAME = "AgentResponseFactory";

    private static void populateBaseSpec(BaseSpec spec, String normalizedText) {
        spec.setNormalizedText(normalizedText);
        spec.setSynthetic(true);
        spec.setSkipped(true);
        spec.setAgentName(FACTORY_AGENT_NAME);
        spec.setTimestamp(System.currentTimeMillis());
        spec.setVersion("1.0");
    }

    // ---------------------------------------------------------
    // REQUIREMENT SPEC (supports user input)
    // ---------------------------------------------------------
    public static RequirementSpec syntheticRequirementSpec(String userInput) {
        RequirementSpec spec = new RequirementSpec();

        String normalized = (userInput == null || userInput.isBlank())
                ? "Synthetic requirement: user skipped requirement phase."
                : "Synthetic requirement (derived from user input): " + userInput;

        populateBaseSpec(spec, normalized);

        spec.setAmbiguities(List.of());
        spec.setAssumptions(List.of("Requirement phase skipped by user"));
        spec.setConstraints(List.of());
        spec.setMissingInfo(List.of());
        spec.setAcceptanceCriteria(List.of());

        return spec;
    }

    // ---------------------------------------------------------
    // TASK SPEC
    // ---------------------------------------------------------
    public static TaskSpec syntheticTaskSpec(String userInput) {
        TaskSpec spec = new TaskSpec();

        String normalized = "Synthetic task spec (no task decomposition performed). "
                + (userInput != null ? "User input: " + userInput : "");

        populateBaseSpec(spec, normalized);

        spec.setTasks(List.of());
        spec.setDependencies(List.of());

        return spec;
    }

    // ---------------------------------------------------------
    // IMPACT ANALYSIS SPEC
    // ---------------------------------------------------------
    public static ImpactAnalysisSpec syntheticImpactAnalysisSpec(String userInput) {
        ImpactAnalysisSpec spec = new ImpactAnalysisSpec();

        String normalized = "Synthetic impact analysis (user skipped impact phase). "
                + (userInput != null ? "User input: " + userInput : "");

        populateBaseSpec(spec, normalized);

        spec.setImpactedModules(List.of());
        spec.setImpactedServices(List.of());
        spec.setImpactedAPIs(List.of());
        spec.setImpactedDataFlows(List.of());
        spec.setRisks(List.of("Impact analysis skipped by user"));

        return spec;
    }

    // ---------------------------------------------------------
    // DESIGN SPEC
    // ---------------------------------------------------------
    public static DesignSpec syntheticDesignSpec(String userInput) {
        DesignSpec spec = new DesignSpec();

        String normalized = "Synthetic design (user skipped design phase). "
                + (userInput != null ? "User input: " + userInput : "");

        populateBaseSpec(spec, normalized);

        spec.setArchitectureOverview("Synthetic design: no design agent executed.");
        spec.setComponents(List.of());
        spec.setDataFlow(List.of());
        spec.setApiDefinitions(List.of());
        spec.setDatabaseSchema(List.of());
        spec.setNonFunctionalRequirements(List.of());
        spec.setRisks(List.of("Design skipped by user"));
        spec.setOpenQuestions(List.of());

        return spec;
    }

    // ---------------------------------------------------------
    // CODE SPEC
    // ---------------------------------------------------------
    public static CodeSpec syntheticCodeSpec(String userInput) {
        CodeSpec spec = new CodeSpec();

        String normalized = "Synthetic code spec (user skipped code phase). "
                + (userInput != null ? "User input: " + userInput : "");

        populateBaseSpec(spec, normalized);

        spec.setFiles(Map.of());
        spec.setLanguage("unknown");
        spec.setFramework("unknown");
        spec.setBuildInstructions("Synthetic code: no code agent executed.");
        spec.setLintWarnings(List.of());

        return spec;
    }

    // ---------------------------------------------------------
    // TEST SPEC
    // ---------------------------------------------------------
    public static TestSpec syntheticTestSpec(String userInput) {
        TestSpec spec = new TestSpec();

        String normalized = "Synthetic test spec (user skipped test phase). "
                + (userInput != null ? "User input: " + userInput : "");

        populateBaseSpec(spec, normalized);

        spec.setUnitTests(List.of());
        spec.setIntegrationTests(List.of());
        spec.setCoverageEstimate(0);
        spec.setMissingTests(List.of("Test phase skipped by user"));

        return spec;
    }

    // ---------------------------------------------------------
    // DOCUMENTATION SPEC
    // ---------------------------------------------------------
    public static DocumentationSpec syntheticDocumentationSpec(String userInput) {
        DocumentationSpec spec = new DocumentationSpec();

        String normalized = "Synthetic documentation spec (user skipped documentation phase). "
                + (userInput != null ? "User input: " + userInput : "");

        populateBaseSpec(spec, normalized);

        spec.setReadme("Synthetic documentation: no documentation agent executed.");
        spec.setApiDocs(List.of());
        spec.setArchitectureDocs(List.of());
        spec.setAdrs(List.of());
        spec.setUsageExamples(List.of());

        return spec;
    }

    // ---------------------------------------------------------
    // RELEASE SPEC
    // ---------------------------------------------------------
    public static ReleaseSpec syntheticReleaseSpec(String userInput) {
        ReleaseSpec spec = new ReleaseSpec();

        String normalized = "Synthetic release spec (user skipped release phase). "
                + (userInput != null ? "User input: " + userInput : "");

        populateBaseSpec(spec, normalized);

        spec.setReleasePlan("Synthetic release: no release agent executed.");
        spec.setEnvironments(List.of());
        spec.setRollbackPlan("Synthetic rollback plan.");
        spec.setDeploymentSteps(List.of());
        spec.setRisks(List.of("Release skipped by user"));

        return spec;
    }

    // ---------------------------------------------------------
    // SUMMARY SPEC
    // ---------------------------------------------------------
    public static SummarySpec syntheticSummarySpec(String userInput) {
        SummarySpec spec = new SummarySpec();

        String normalized = "Synthetic summary spec (user skipped summary phase). "
                + (userInput != null ? "User input: " + userInput : "");

        populateBaseSpec(spec, normalized);

        spec.setNotes("Synthetic summary: no summary agent executed.");

        return spec;
    }
}
