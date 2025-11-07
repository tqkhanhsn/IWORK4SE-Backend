package vn.iwork4se.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iwork4se.controller.request.MessageCreationRequest;
import vn.iwork4se.controller.response.ConversationResponse;
import vn.iwork4se.controller.response.MessageResponse;
import vn.iwork4se.model.User;
import vn.iwork4se.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(
            Authentication authentication,
            @RequestBody MessageCreationRequest request) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String senderId = authenticatedUser.getId();
        MessageResponse response = messageService.sendMessage(senderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/send-image")
    public ResponseEntity<MessageResponse> sendImageMessage(
            Authentication authentication,
            @RequestParam String receiverId,
            @RequestParam("file") MultipartFile file) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String senderId = authenticatedUser.getId();
        MessageResponse response = messageService.sendImageMessage(senderId, receiverId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<MessageResponse>> getConversationMessages(
            Authentication authentication,
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authenticatedUser.getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageResponse> messages = messageService.getConversationMessages(userId, conversationId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversations")
    public ResponseEntity<Page<ConversationResponse>> getUserConversations(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authenticatedUser.getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationResponse> conversations = messageService.getUserConversations(userId, pageable);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/active")
    public ResponseEntity<List<ConversationResponse>> getActiveConversations(
            Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authenticatedUser.getId();
        List<ConversationResponse> conversations = messageService.getActiveConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @PutMapping("/conversation/{conversationId}/mark-as-read")
    public ResponseEntity<Void> markMessagesAsRead(
            Authentication authentication,
            @PathVariable Long conversationId) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authenticatedUser.getId();
        messageService.markMessagesAsRead(conversationId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authenticatedUser.getId();
        Long unreadCount = messageService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(unreadCount);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            Authentication authentication,
            @PathVariable Long messageId) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authenticatedUser.getId();
        messageService.deleteMessage(messageId, userId);
        return ResponseEntity.noContent().build();
    }
}
