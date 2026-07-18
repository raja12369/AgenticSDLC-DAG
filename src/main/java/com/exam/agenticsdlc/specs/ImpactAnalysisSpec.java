package com.exam.agenticsdlc.specs;

import java.util.List;

public class ImpactAnalysisSpec extends BaseSpec {

    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> impactedModules;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> impactedServices;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> impactedAPIs;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> impactedDataFlows;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> risks;

    public List<String> getImpactedModules() { return impactedModules; }
    public void setImpactedModules(List<String> impactedModules) { this.impactedModules = impactedModules; }

    public List<String> getImpactedServices() { return impactedServices; }
    public void setImpactedServices(List<String> impactedServices) { this.impactedServices = impactedServices; }

    public List<String> getImpactedAPIs() { return impactedAPIs; }
    public void setImpactedAPIs(List<String> impactedAPIs) { this.impactedAPIs = impactedAPIs; }

    public List<String> getImpactedDataFlows() { return impactedDataFlows; }
    public void setImpactedDataFlows(List<String> impactedDataFlows) { this.impactedDataFlows = impactedDataFlows; }

    public List<String> getRisks() { return risks; }
    public void setRisks(List<String> risks) { this.risks = risks; }
}
