package com.studymate.service;

import com.studymate.dto.ChatRequest;
import com.studymate.dto.ChatResponse;

public interface ChatService {

    ChatResponse sendMessage(ChatRequest request);
}
