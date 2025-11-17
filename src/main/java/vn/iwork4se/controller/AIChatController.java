package vn.iwork4se.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.controller.request.AIChatRequest;
import vn.iwork4se.controller.response.AIChatResponse;
import vn.iwork4se.model.User;
import vn.iwork4se.service.GeminiService;

@RestController
@RequestMapping("/api/ai-chat")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN', 'EMPLOYER')")
public class AIChatController {
    
    private final GeminiService geminiService;
    
    @PostMapping("/send")
    public ResponseEntity<AIChatResponse> sendMessage(
            Authentication authentication,
            @RequestBody AIChatRequest request) {
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Validate input
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            // Generate AI response
            String aiResponse = geminiService.generateResponse(
                    request.getMessage().trim(),
                    request.getConversationHistory()
            );
            
            // Build response with updated conversation history
            // In a real implementation, you might want to store this in database
            String updatedHistory = request.getConversationHistory() != null 
                    ? request.getConversationHistory() 
                    : "";
            
            AIChatResponse response = AIChatResponse.builder()
                    .response(aiResponse)
                    .conversationHistory(updatedHistory)
                    .build();
            
            System.out.println("[AI-CHAT] Response generated: " + (aiResponse != null ? aiResponse.substring(0, Math.min(100, aiResponse.length())) : "null"));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

