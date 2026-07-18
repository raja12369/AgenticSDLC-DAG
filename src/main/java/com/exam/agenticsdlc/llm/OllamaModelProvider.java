package com.exam.agenticsdlc.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class OllamaModelProvider {

    private final Map<String, ChatLanguageModel> cache = new ConcurrentHashMap<>();

    public ChatLanguageModel get(String modelName) {

        // Return cached model if already created
        if (cache.containsKey(modelName)) {
            return cache.get(modelName);
        }

        // Create new model instance
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(modelName)
                .build();

        // Cache it for future use
        cache.put(modelName, model);

        return model;
    }
}
