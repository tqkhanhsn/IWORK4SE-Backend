package vn.iwork4se.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long id;
    private String user1Id;
    private String user1Name;
    private String user2Id;
    private String user2Name;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private Boolean isActive;
}
