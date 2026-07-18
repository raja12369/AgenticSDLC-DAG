package com.exam.agenticsdlc.specs;

import java.util.List;

public class DocumentationSpec extends BaseSpec {

    private String readme;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> apiDocs;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> architectureDocs;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> adrs;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> usageExamples;

    public String getReadme() { return readme; }
    public void setReadme(String readme) { this.readme = readme; }

    public List<String> getApiDocs() { return apiDocs; }
    public void setApiDocs(List<String> apiDocs) { this.apiDocs = apiDocs; }

    public List<String> getArchitectureDocs() { return architectureDocs; }
    public void setArchitectureDocs(List<String> architectureDocs) { this.architectureDocs = architectureDocs; }

    public List<String> getAdrs() { return adrs; }
    public void setAdrs(List<String> adrs) { this.adrs = adrs; }

    public List<String> getUsageExamples() { return usageExamples; }
    public void setUsageExamples(List<String> usageExamples) { this.usageExamples = usageExamples; }
}
