package com.artagram.ai.proveedor;

import com.artagram.ai.prompt.PromptIa;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class GroqProveedorIa implements ProveedorIa {

    private static final Logger log = LoggerFactory.getLogger(GroqProveedorIa.class);

    private final String apiKey;
    private final String model;
    private final FakeProveedorIa fallback;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GroqProveedorIa(@Value("${artagram.ia.groq-api-key:}") String apiKey,
                           @Value("${artagram.ia.groq-model:qwen/qwen3.8-27b}") String model,
                           FakeProveedorIa fallback,
                           ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.model = model;
        this.fallback = fallback;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String getNombreModelo() {
        return (apiKey != null && !apiKey.isBlank()) ? model : fallback.getNombreModelo();
    }

    @Override
    public boolean soportaVision() {
        return true;
    }

    @Override
    public String completar(PromptIa prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.info("Clave GROQ_API_KEY no configurada; utilizando FakeProveedorIa");
            return fallback.completar(prompt);
        }

        try {
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", model);
            requestBody.put("temperature", prompt.getTemperatura());
            requestBody.put("max_tokens", prompt.getMaxTokens());
            if (prompt.isModoJson()) {
                requestBody.put("response_format", Map.of("type", "json_object"));
            }

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", prompt.getSystemPrompt()));

            List<Map<String, Object>> userContent = new ArrayList<>();
            userContent.add(Map.of("type", "text", "text", prompt.getInstruccionUsuario()));

            if (prompt.getImagenDataUriOUrl() != null) {
                userContent.add(Map.of("type", "image_url", "image_url", Map.of("url", prompt.getImagenDataUriOUrl())));
            }
            messages.add(Map.of("role", "user", "content", userContent));
            requestBody.put("messages", messages);

            String jsonPayload = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                log.warn("Error HTTP desde Groq ({}): {}, usando fallback", response.statusCode(), response.body());
                return fallback.completar(prompt);
            }

            JsonNode root = objectMapper.readTree(response.body());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception ex) {
            log.error("Excepción al consultar API de Groq: {}, usando fallback", ex.getMessage());
            return fallback.completar(prompt);
        }
    }
}
