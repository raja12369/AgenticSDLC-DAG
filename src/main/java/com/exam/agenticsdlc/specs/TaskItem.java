package com.exam.agenticsdlc.specs;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

public class TaskItem {

    private String id;

    private String name;

    private String description;

    @JsonAlias({"type", "taskType", "task_type", "stage"})
    private String type;

    @JsonAlias({"dependencies", "dependsOn"})
    private List<String> dependencies;

    @JsonAlias({"parentTaskId", "parent", "parents"})
    private String parentTaskId;

    @JsonAlias({"inputs"})
    private List<TaskField> inputs;

    @JsonAlias({"outputs"})
    private List<TaskField> outputs;

    // ---------------------------
    // Getters and Setters
    // ---------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public List<TaskField> getInputs() {
        return inputs;
    }

    public void setInputs(List<TaskField> inputs) {
        this.inputs = inputs;
    }

    public List<TaskField> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TaskField> outputs) {
        this.outputs = outputs;
    }
}
