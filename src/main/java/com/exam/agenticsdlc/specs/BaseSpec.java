package com.exam.agenticsdlc.specs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseSpec {

    private long timestamp;
    private String version;
    private String prompt;
    private String agentName;
    private String responseJson;
    private boolean synthetic;
    private boolean skipped;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> ambiguities;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> missingInfo;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> constraints;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> assumptions;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> acceptanceCriteria;
    private String normalizedText;
    private String raw;



    public BaseSpec() {} // REQUIRED

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getNormalizedText() {
        return normalizedText;
    }


    public void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public String getResponseJson() { return responseJson; }
    public void setResponseJson(String responseJson) { this.responseJson = responseJson; }

    public boolean isSynthetic() { return synthetic; }
    public void setSynthetic(boolean synthetic) { this.synthetic = synthetic; }

    public boolean isSkipped() { return skipped; }
    public void setSkipped(boolean skipped) { this.skipped = skipped; }

    public List<String> getAmbiguities() { return ambiguities; }
    public void setAmbiguities(List<String> ambiguities) { this.ambiguities = ambiguities; }

    public List<String> getMissingInfo() { return missingInfo; }
    public void setMissingInfo(List<String> missingInfo) { this.missingInfo = missingInfo; }

    public List<String> getConstraints() { return constraints; }
    public void setConstraints(List<String> constraints) { this.constraints = constraints; }

    public List<String> getAssumptions() { return assumptions; }
    public void setAssumptions(List<String> assumptions) { this.assumptions = assumptions; }

    public List<String> getAcceptanceCriteria() { return acceptanceCriteria; }
    public void setAcceptanceCriteria(List<String> acceptanceCriteria) { this.acceptanceCriteria = acceptanceCriteria; }
}
