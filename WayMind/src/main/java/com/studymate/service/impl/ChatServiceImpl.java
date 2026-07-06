package com.studymate.service.impl;

import com.studymate.dto.ChatRequest;
import com.studymate.dto.ChatResponse;
import com.studymate.entity.ChatHistory;
import com.studymate.entity.User;
import com.studymate.mapper.ChatHistoryMapper;
import com.studymate.mapper.UserMapper;
import com.studymate.service.ChatService;
import com.studymate.util.QianWenClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private QianWenClient qianWenClient;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChatHistoryMapper chatHistoryMapper;

    @Override
    public ChatResponse sendMessage(ChatRequest request) {
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        Long userId = request.getUserId();
        if (userId == null) {
            userId = createDefaultUser();
        } else {
            User user = userMapper.selectById(userId);
            if (user == null) {
                userId = createDefaultUser();
            }
        }

        String aiReply;
        try {
            aiReply = qianWenClient.chat(request.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Failed to call QianWen API", e);
        }

        saveChatHistory(userId, request.getMessage(), aiReply);

        ChatResponse response = new ChatResponse();
        response.setReply(aiReply);
        response.setUserId(userId);
        return response;
    }

    private Long createDefaultUser() {
        User user = new User();
        user.setNickname("默认用户");
        user.setAvatar("");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user.getId();
    }

    private void saveChatHistory(Long userId, String userMessage, String aiResponse) {
        ChatHistory history = new ChatHistory();
        history.setUserId(userId);
        history.setUserMessage(userMessage);
        history.setAiResponse(aiResponse);
        history.setCreatedAt(LocalDateTime.now());
        chatHistoryMapper.insert(history);
    }
}
