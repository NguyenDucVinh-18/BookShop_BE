package vn.edu.iuh.fit.bookshop_be.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String API_KEY;

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    public String askGemini(String userMessage, byte[] imageBytes) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // Prompt đóng vai trò nhân viên bán sách
        String systemPrompt = "Bạn là một nhân viên bán sách. Hãy trả lời khách hàng một cách lịch sự, rõ ràng và hữu ích.";

        // encode ảnh sang base64
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Request body (gồm text + image)
        Map<String, Object> request = new HashMap<>();
        request.put("contents", new Object[]{
                Map.of("parts", new Object[]{
                        Map.of("text", systemPrompt + "\nKhách hàng hỏi: " + userMessage),
                        Map.of("inlineData", Map.of(
                                "mimeType", "image/png",   // hoặc "image/jpeg"
                                "data", base64Image
                        ))
                })
        });

        Map response = restTemplate.postForObject(GEMINI_API_URL + API_KEY, request, Map.class);

        if (response != null && response.containsKey("candidates")) {
            Map firstCandidate = (Map) ((java.util.List) response.get("candidates")).get(0);
            Map content = (Map) firstCandidate.get("content");
            java.util.List parts = (java.util.List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            return (String) firstPart.get("text");
        }
        return "Xin lỗi, hiện tại tôi chưa thể trả lời.";
    }
}
