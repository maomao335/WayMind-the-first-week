package com.studymate.controller;

import com.studymate.dto.ApiResponse;
import com.studymate.dto.ChatRequest;
import com.studymate.dto.ChatResponse;
import com.studymate.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ApiResponse<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        ChatResponse response = chatService.sendMessage(request);
        return ApiResponse.success(response);
    }
}
