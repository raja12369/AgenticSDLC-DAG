package com.exam.agenticsdlc.specs;

public class TestCase {

    private String name;
    private String description;
    private Object input;
    private Object expectedOutput;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Object getInput() { return input; }
    public void setInput(Object input) { this.input = input; }

    public Object getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(Object expectedOutput) { this.expectedOutput = expectedOutput; }
}
