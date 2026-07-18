package com.exam.agenticsdlc.specs;

import java.util.Map;
import java.util.List;

public class CodeSpec extends BaseSpec {

    private Map<String, String> files;
    private String language;
    private String framework;
    private String buildInstructions;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> lintWarnings;

    public Map<String, String> getFiles() { return files; }
    public void setFiles(Map<String, String> files) { this.files = files; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getFramework() { return framework; }
    public void setFramework(String framework) { this.framework = framework; }

    public String getBuildInstructions() { return buildInstructions; }
    public void setBuildInstructions(String buildInstructions) { this.buildInstructions = buildInstructions; }

    public List<String> getLintWarnings() { return lintWarnings; }
    public void setLintWarnings(List<String> lintWarnings) { this.lintWarnings = lintWarnings; }
}
