package org.surro.ecomapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.surro.ecomapp.service.ChatService;

@RestController()
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/ask")
    public ResponseEntity<String> message(@RequestParam String message) {
        return ResponseEntity.ok(chatService.message(message));
    }


}
