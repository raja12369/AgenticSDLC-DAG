package com.exam.agenticsdlc.specs;

import java.util.List;

public class DesignSpec extends BaseSpec {

    private String architectureOverview;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> components;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> dataFlow;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> apiDefinitions;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> databaseSchema;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> nonFunctionalRequirements;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> risks;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> openQuestions;

    public String getArchitectureOverview() { return architectureOverview; }
    public void setArchitectureOverview(String architectureOverview) { this.architectureOverview = architectureOverview; }

    public List<String> getComponents() { return components; }
    public void setComponents(List<String> components) { this.components = components; }

    public List<String> getDataFlow() { return dataFlow; }
    public void setDataFlow(List<String> dataFlow) { this.dataFlow = dataFlow; }

    public List<String> getApiDefinitions() { return apiDefinitions; }
    public void setApiDefinitions(List<String> apiDefinitions) { this.apiDefinitions = apiDefinitions; }

    public List<String> getDatabaseSchema() { return databaseSchema; }
    public void setDatabaseSchema(List<String> databaseSchema) { this.databaseSchema = databaseSchema; }

    public List<String> getNonFunctionalRequirements() { return nonFunctionalRequirements; }
    public void setNonFunctionalRequirements(List<String> nonFunctionalRequirements) { this.nonFunctionalRequirements = nonFunctionalRequirements; }

    public List<String> getRisks() { return risks; }
    public void setRisks(List<String> risks) { this.risks = risks; }

    public List<String> getOpenQuestions() { return openQuestions; }
    public void setOpenQuestions(List<String> openQuestions) { this.openQuestions = openQuestions; }
}
