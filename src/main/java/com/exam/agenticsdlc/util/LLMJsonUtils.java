package com.exam.agenticsdlc.util;

public class LLMJsonUtils {

    /**
     * Extracts the first valid JSON object from an LLM response.
     * Works even if the model returns text + JSON + markdown.
     */
    public static String extractJson(String text) {
        if (text == null || text.isBlank()) {
            throw new RuntimeException("LLM response is empty");
        }

        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");

        if (start == -1 || end == -1 || end <= start) {
            throw new RuntimeException("No JSON object found in LLM response:\n" + text);
        }

        return text.substring(start, end + 1);
    }

    /**
     * Returns a strict JSON-only prompt wrapper.
     * Use this in every agent to force JSON output.
     */
    public static String strictJsonPrompt(String format, String userInput) {
        return """
        Return ONLY valid JSON. No explanation, no markdown, no text outside JSON.

        Format:
        %s

        User input: %s
        """.formatted(format, userInput);
    }
}
