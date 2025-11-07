package vn.iwork4se.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vn.iwork4se.controller.response.MessageResponse;
import vn.iwork4se.service.MessageService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/send-message/{receiverId}")
    @SendTo("/topic/messages/{receiverId}")
    public MessageResponse sendMessage(
            @DestinationVariable String receiverId,
            @Payload MessageResponse message) {
        log.info("Message received from {} to {}", message.getSenderId(), receiverId);
        return message;
    }

    @MessageMapping("/mark-as-read/{conversationId}/{userId}")
    public void markAsRead(
            @DestinationVariable Long conversationId,
            @DestinationVariable String userId) {
        messageService.markMessagesAsRead(conversationId, userId);
        messagingTemplate.convertAndSend("/topic/messages-read/" + conversationId, "Messages marked as read");
        log.info("Messages marked as read for conversation {}", conversationId);
    }

    @MessageMapping("/typing/{receiverId}")
    @SendTo("/topic/typing/{receiverId}")
    public TypingNotification typingNotification(
            @DestinationVariable String receiverId,
            @Payload TypingNotification notification) {
        return notification;
    }

    public static class TypingNotification {
        private String userId;
        private boolean isTyping;

        public TypingNotification() {}

        public TypingNotification(String userId, boolean isTyping) {
            this.userId = userId;
            this.isTyping = isTyping;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public boolean isTyping() {
            return isTyping;
        }

        public void setTyping(boolean typing) {
            isTyping = typing;
        }
    }
}
