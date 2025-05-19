package br.edu.utfpr.pb.pw45s.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatGPTService {
    private final String API_URL = "https://api.openai.com/v1/chat/completions";
    @Value("${openai.api.key}")
    private String apiKey;



    public String ask(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", userMessage));
        request.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content");
    }
}

