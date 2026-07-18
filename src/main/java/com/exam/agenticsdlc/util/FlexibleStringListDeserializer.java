package com.exam.agenticsdlc.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * LLMs are frequently told "respond with an array of strings" and instead return an array
 * of small objects (e.g. {"endpoint": "...", "description": "..."}), because a richer shape
 * feels more natural for the content being described. Rather than hard-failing the whole
 * phase on that mismatch, this deserializer accepts whatever shape shows up for a
 * List&lt;String&gt; field and normalizes it to readable strings:
 *
 *   ["a", "b"]                              -> ["a", "b"]
 *   [{"endpoint":"/x","description":"y"}]   -> ["endpoint: /x, description: y"]
 *   "single string"                         -> ["single string"]
 *   null                                    -> []
 *
 * Applied via @JsonDeserialize(using = FlexibleStringListDeserializer.class) on any
 * List&lt;String&gt; field that an LLM populates.
 */
public class FlexibleStringListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        List<String> result = new ArrayList<>();

        if (node == null || node.isNull()) {
            return result;
        }

        if (node.isArray()) {
            for (JsonNode item : node) {
                result.add(nodeToString(item));
            }
        } else if (node.isObject()) {
            // A single object where an array was expected - treat each entry as one item.
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                result.add(entry.getKey() + ": " + entry.getValue().asText());
            }
        } else if (node.isTextual()) {
            result.add(node.asText());
        } else {
            result.add(node.asText());
        }

        return result;
    }

    private String nodeToString(JsonNode item) {
        if (item.isTextual()) {
            return item.asText();
        }
        if (item.isObject()) {
            StringBuilder sb = new StringBuilder();
            Iterator<Map.Entry<String, JsonNode>> fields = item.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (sb.length() > 0) sb.append(", ");
                sb.append(entry.getKey()).append(": ").append(entry.getValue().asText());
            }
            return sb.toString();
        }
        return item.asText();
    }
}
