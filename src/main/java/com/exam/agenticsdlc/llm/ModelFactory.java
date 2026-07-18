package com.exam.agenticsdlc.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ModelFactory {

    private final Map<String, ChatLanguageModel> cache = new ConcurrentHashMap<>();

    public ChatLanguageModel get(ProviderType provider, ModelName modelName) {

        String key = provider + ":" + modelName.name();

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        ChatLanguageModel model = switch (provider) {

            case OLLAMA -> OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName(modelName.ollama())
                    .build();

            case OPENAI -> throw new UnsupportedOperationException("OpenAI provider not implemented yet");

            case AZURE_OPENAI -> throw new UnsupportedOperationException("Azure provider not implemented yet");
        };

        cache.put(key, model);
        return model;
    }
}
