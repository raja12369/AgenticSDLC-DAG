package com.exam.agenticsdlc.specs;

import java.util.List;

public class ReleaseSpec extends BaseSpec {

    private String releasePlan;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> environments;
    private String rollbackPlan;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> deploymentSteps;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> risks;

    public String getReleasePlan() { return releasePlan; }
    public void setReleasePlan(String releasePlan) { this.releasePlan = releasePlan; }

    public List<String> getEnvironments() { return environments; }
    public void setEnvironments(List<String> environments) { this.environments = environments; }

    public String getRollbackPlan() { return rollbackPlan; }
    public void setRollbackPlan(String rollbackPlan) { this.rollbackPlan = rollbackPlan; }

    public List<String> getDeploymentSteps() { return deploymentSteps; }
    public void setDeploymentSteps(List<String> deploymentSteps) { this.deploymentSteps = deploymentSteps; }

    public List<String> getRisks() { return risks; }
    public void setRisks(List<String> risks) { this.risks = risks; }
}
