package com.exam.agenticsdlc.llm;

public enum ModelName {

    REQUIREMENT("llama3"),
    DESIGN("deepseek-coder-v2"),
    CODE("deepseek-coder-v2"),
    TEST("qwen2.5-coder"),
    DOCUMENTATION("llama3");


    private final String ollamaName;

    ModelName(String ollamaName) {
        this.ollamaName = ollamaName;
    }

    public String ollama() {
        return ollamaName;
    }
}

