package vn.edu.iuh.fit.bookshop_be.controllers;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.bookshop_be.services.GeminiService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping(value = "/ask", consumes = {"multipart/form-data"})
    public Map<String, String> chat(
            @RequestPart("message") String message,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        String reply = geminiService.askGemini(message, image.getBytes());
        return Map.of("reply", reply);
    }
}
