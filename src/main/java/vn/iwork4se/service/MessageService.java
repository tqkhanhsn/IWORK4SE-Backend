package vn.iwork4se.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.iwork4se.controller.request.MessageCreationRequest;
import vn.iwork4se.controller.response.ConversationResponse;
import vn.iwork4se.controller.response.MessageResponse;

import java.util.List;

public interface MessageService {
    MessageResponse sendMessage(String senderId, MessageCreationRequest request);
    MessageResponse sendImageMessage(String senderId, String receiverId, MultipartFile imageFile);
    Page<MessageResponse> getConversationMessages(String userId, Long conversationId, Pageable pageable);
    Page<ConversationResponse> getUserConversations(String userId, Pageable pageable);
    List<ConversationResponse> getActiveConversations(String userId);
    void markMessagesAsRead(Long conversationId, String userId);
    Long getUnreadMessageCount(String userId);
    void deleteMessage(Long messageId, String userId);
}
