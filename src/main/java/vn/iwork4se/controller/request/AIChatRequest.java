package vn.iwork4se.controller.request;

import lombok.Data;

@Data
public class AIChatRequest {
    private String message;
    private String conversationHistory; // Optional: JSON string of previous messages
}

