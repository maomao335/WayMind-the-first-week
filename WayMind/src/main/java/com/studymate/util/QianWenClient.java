package com.studymate.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class QianWenClient {

    @Value("${qianwen.api-key}")
    private String apiKey;

    @Value("${qianwen.api-url:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chat(String message) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "qwen-turbo");

            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode userMessage = objectMapper.createObjectNode();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);
            requestBody.set("messages", messages);

            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(requestBody), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            String response = httpClient.execute(httpPost, responseHandler -> {
                return new String(responseHandler.getEntity().getContent().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            });

            JsonNode responseJson = objectMapper.readTree(response);
            JsonNode choices = responseJson.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode messageNode = firstChoice.get("message");
                if (messageNode != null) {
                    JsonNode content = messageNode.get("content");
                    if (content != null && !content.isNull()) {
                        return content.asText();
                    }
                }
            }
            throw new RuntimeException("Failed to get response from QianWen API");
        }
    }
}
