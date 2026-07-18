package com.exam.agenticsdlc.specs;

import java.util.List;

public class TaskSpec extends BaseSpec {

    private List<TaskItem> tasks;
    private List<TaskDependency> dependencies;


    public List<TaskItem> getTasks() { return tasks; }
    public void setTasks(List<TaskItem> tasks) { this.tasks = tasks; }

    public List<TaskDependency> getDependencies() { return dependencies; }
    public void setDependencies(List<TaskDependency> dependencies) { this.dependencies = dependencies; }
}
