package vn.iwork4se.service;

public interface GeminiService {
    String generateResponse(String userMessage, String conversationHistory);
}

