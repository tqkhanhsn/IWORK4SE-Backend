package vn.iwork4se.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iwork4se.common.MessageType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String content;
    private String imageUrl;
    private MessageType messageType;
    private LocalDateTime sentAt;
    private Boolean isRead;
}
