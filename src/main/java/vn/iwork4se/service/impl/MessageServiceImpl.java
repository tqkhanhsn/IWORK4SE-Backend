package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iwork4se.common.MessageType;
import vn.iwork4se.common.UserType;
import vn.iwork4se.controller.request.MessageCreationRequest;
import vn.iwork4se.controller.response.ConversationResponse;
import vn.iwork4se.controller.response.MessageResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Conversation;
import vn.iwork4se.model.Message;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.ConversationRepository;
import vn.iwork4se.repository.MessageRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.MessageService;
import vn.iwork4se.service.SupabaseStorageService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SupabaseStorageService supabaseStorageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public MessageResponse sendMessage(String senderId, MessageCreationRequest request) {
        log.debug("[MESSAGING] Starting to send text message from user: {} to user: {}", senderId, request.getReceiverId());

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        // Validate roles: Only ADMIN and EMPLOYER can send/receive messages
        if (sender.getUserType() != UserType.ADMIN && sender.getUserType() != UserType.EMPLOYER) {
            log.warn("[MESSAGING] Unauthorized sender role: {} for user: {}", sender.getUserType(), senderId);
            throw new RuntimeException("Only ADMIN and EMPLOYER can send messages");
        }
        if (receiver.getUserType() != UserType.ADMIN && receiver.getUserType() != UserType.EMPLOYER) {
            log.warn("[MESSAGING] Unauthorized receiver role: {} for user: {}", receiver.getUserType(), request.getReceiverId());
            throw new RuntimeException("Only ADMIN and EMPLOYER can receive messages");
        }
        
        // Kiểm tra trạng thái sender nếu là EMPLOYER - chỉ cho phép ACTIVE chat
        if (sender.getUserType() == UserType.EMPLOYER) {
            if (sender.getUserStatus() == null || sender.getUserStatus() != vn.iwork4se.common.UserStatus.ACTIVE) {
                if (sender.getUserStatus() == vn.iwork4se.common.UserStatus.INACTIVE) {
                    throw new RuntimeException("Tài khoản của bạn đang bị tạm khóa. Vui lòng kích hoạt tài khoản để tiếp tục sử dụng dịch vụ.");
                }
                throw new RuntimeException("Bạn không thể gửi tin nhắn với trạng thái tài khoản hiện tại");
            }
        }

        // Find active conversation between users
        List<Conversation> activeConversations = conversationRepository
                .findActiveConversationsBetweenUsers(senderId, request.getReceiverId());
        
        Conversation conversation;
        if (!activeConversations.isEmpty()) {
            // Use the most recent active conversation
            conversation = activeConversations.get(0);
            log.debug("[MESSAGING] Using existing active conversation {} between {} and {}", 
                    conversation.getId(), senderId, request.getReceiverId());
        } else {
            // Deactivate all old conversations between these users
            List<Conversation> allConversations = conversationRepository
                    .findAllConversationsBetweenUsers(senderId, request.getReceiverId());
            allConversations.forEach(oldConv -> {
                oldConv.setIsActive(false);
                conversationRepository.save(oldConv);
            });
            
            // Create new active conversation
            log.debug("[MESSAGING] Creating new conversation between {} and {}", senderId, request.getReceiverId());
            conversation = Conversation.builder()
                    .user1(sender)
                    .user2(receiver)
                    .isActive(true)
                    .build();
            conversation = conversationRepository.save(conversation);
        }

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .messageType(MessageType.TEXT)
                .isRead(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);

        log.info("[MESSAGING] Text message saved - ID: {}, From: {}, To: {}, Content: {}",
                savedMessage.getId(), senderId, request.getReceiverId(), request.getContent());

        MessageResponse messageResponse = convertToMessageResponse(savedMessage);

        log.debug("[SOCKET] Broadcasting message to topic: /topic/messages/{}", request.getReceiverId());
        messagingTemplate.convertAndSend(
                "/topic/messages/" + request.getReceiverId(),
                messageResponse
        );
        log.info("[SOCKET] Message {} sent successfully to WebSocket topic for user: {}",
                savedMessage.getId(), request.getReceiverId());

        return messageResponse;
    }

    @Override
    public MessageResponse sendImageMessage(String senderId, String receiverId, MultipartFile imageFile) {
        log.debug("[MESSAGING] Starting to send image message from user: {} to user: {}", senderId, receiverId);

        if (imageFile.isEmpty() || !isValidImageFile(imageFile)) {
            log.warn("[MESSAGING] Invalid image file attempted to upload by user: {}", senderId);
            throw new RuntimeException("Invalid image file");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        // Validate roles: Only ADMIN and EMPLOYER can send/receive messages
        if (sender.getUserType() != UserType.ADMIN && sender.getUserType() != UserType.EMPLOYER) {
            log.warn("[MESSAGING] Unauthorized sender role: {} for user: {}", sender.getUserType(), senderId);
            throw new RuntimeException("Only ADMIN and EMPLOYER can send messages");
        }
        if (receiver.getUserType() != UserType.ADMIN && receiver.getUserType() != UserType.EMPLOYER) {
            log.warn("[MESSAGING] Unauthorized receiver role: {} for user: {}", receiver.getUserType(), receiverId);
            throw new RuntimeException("Only ADMIN and EMPLOYER can receive messages");
        }
        
        // Kiểm tra trạng thái sender nếu là EMPLOYER - chỉ cho phép ACTIVE chat
        if (sender.getUserType() == UserType.EMPLOYER) {
            if (sender.getUserStatus() == null || sender.getUserStatus() != vn.iwork4se.common.UserStatus.ACTIVE) {
                if (sender.getUserStatus() == vn.iwork4se.common.UserStatus.INACTIVE) {
                    throw new RuntimeException("Tài khoản của bạn đang bị tạm khóa. Vui lòng kích hoạt tài khoản để tiếp tục sử dụng dịch vụ.");
                }
                throw new RuntimeException("Bạn không thể gửi tin nhắn với trạng thái tài khoản hiện tại");
            }
        }

        log.debug("[SUPABASE] Uploading image file: {} (size: {} bytes)",
                imageFile.getOriginalFilename(), imageFile.getSize());
        String imageUrl = supabaseStorageService.uploadFile(imageFile, "messages/" + senderId);
        String filePath = "messages/" + senderId + "/" + imageFile.getOriginalFilename();
        log.info("[SUPABASE] Image uploaded successfully - URL: {}", imageUrl);

        // Find active conversation between users
        List<Conversation> activeConversations = conversationRepository
                .findActiveConversationsBetweenUsers(senderId, receiverId);
        
        Conversation conversation;
        if (!activeConversations.isEmpty()) {
            // Use the most recent active conversation
            conversation = activeConversations.get(0);
            log.debug("[MESSAGING] Using existing active conversation {} for image message between {} and {}", 
                    conversation.getId(), senderId, receiverId);
        } else {
            // Deactivate all old conversations between these users
            List<Conversation> allConversations = conversationRepository
                    .findAllConversationsBetweenUsers(senderId, receiverId);
            allConversations.forEach(oldConv -> {
                oldConv.setIsActive(false);
                conversationRepository.save(oldConv);
            });
            
            // Create new active conversation
            log.debug("[MESSAGING] Creating new conversation for image message between {} and {}", senderId, receiverId);
            conversation = Conversation.builder()
                    .user1(sender)
                    .user2(receiver)
                    .isActive(true)
                    .build();
            conversation = conversationRepository.save(conversation);
        }

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .receiver(receiver)
                .imageUrl(imageUrl)
                .filePath(filePath)
                .messageType(MessageType.IMAGE)
                .isRead(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);

        log.info("[MESSAGING] Image message saved - ID: {}, From: {}, To: {}, ImageURL: {}",
                savedMessage.getId(), senderId, receiverId, imageUrl);

        MessageResponse messageResponse = convertToMessageResponse(savedMessage);

        log.debug("[SOCKET] Broadcasting image message to topic: /topic/messages/{}", receiverId);
        messagingTemplate.convertAndSend(
                "/topic/messages/" + receiverId,
                messageResponse
        );
        log.info("[SOCKET] Image message {} sent successfully to WebSocket topic for user: {}",
                savedMessage.getId(), receiverId);

        return messageResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getConversationMessages(String userId, Long conversationId, Pageable pageable) {
        log.debug("[MESSAGING] Fetching messages for user: {} from conversation: {}", userId, conversationId);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        if (!conversation.getUser1().getId().equals(userId) && !conversation.getUser2().getId().equals(userId)) {
            log.warn("[MESSAGING] Unauthorized access attempt by user: {} to conversation: {}", userId, conversationId);
            throw new ResourceNotFoundException("Unauthorized access to conversation");
        }

        Page<MessageResponse> messages = messageRepository.findMessagesByConversation(conversationId, pageable)
                .map(this::convertToMessageResponse);

        log.info("[MESSAGING] Retrieved {} messages from conversation: {} for user: {}",
                messages.getNumberOfElements(), conversationId, userId);

        return messages;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationResponse> getUserConversations(String userId, Pageable pageable) {
        log.debug("[MESSAGING] Fetching conversations for user: {}", userId);
        return conversationRepository.findConversationsByUser(userId, pageable)
                .map(conv -> convertToConversationResponse(conv, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getActiveConversations(String userId) {
        log.debug("[MESSAGING] Fetching active conversations for user: {}", userId);
        return conversationRepository.findActiveConversationsByUser(userId)
                .stream()
                .map(conv -> convertToConversationResponse(conv, userId))
                .collect(Collectors.toList());
    }

    @Override
    public void markMessagesAsRead(Long conversationId, String userId) {
        log.debug("[MESSAGING] Marking messages as read for user: {} in conversation: {}", userId, conversationId);

        List<Message> unreadMessages = messageRepository.findUnreadMessagesByConversation(conversationId, userId);
        unreadMessages.forEach(msg -> msg.setIsRead(true));
        messageRepository.saveAll(unreadMessages);

        log.info("[MESSAGING] Marked {} messages as read for user: {} in conversation: {}",
                unreadMessages.size(), userId, conversationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadMessageCount(String userId) {
        log.debug("[MESSAGING] Fetching unread message count for user: {}", userId);
        Long unreadCount = messageRepository.countUnreadMessages(userId);
        log.info("[MESSAGING] User: {} has {} unread messages", userId, unreadCount);
        return unreadCount;
    }

    @Override
    public void deleteMessage(Long messageId, String userId) {
        log.debug("[MESSAGING] Attempting to delete message: {} by user: {}", messageId, userId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getSender().getId().equals(userId)) {
            log.warn("[MESSAGING] Unauthorized delete attempt for message: {} by user: {}", messageId, userId);
            throw new RuntimeException("Only sender can delete message");
        }

        if ("IMAGE".equals(message.getMessageType()) && message.getFilePath() != null) {
            log.debug("[SUPABASE] Deleting image file from Supabase: {}", message.getFilePath());
            supabaseStorageService.deleteFile(message.getFilePath());
            log.info("[SUPABASE] Image file deleted: {}", message.getFilePath());
        }

        messageRepository.delete(message);
        log.info("[MESSAGING] Message {} deleted successfully by user: {}", messageId, userId);
    }

    private MessageResponse convertToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .imageUrl(message.getImageUrl())
                .messageType(message.getMessageType())
                .sentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .build();
    }

    private ConversationResponse convertToConversationResponse(Conversation conversation, String currentUserId) {
        // Calculate unread count for the current user (only messages where current user is the receiver and isRead = false)
        int unreadCount = messageRepository.findUnreadMessagesByConversation(conversation.getId(), currentUserId).size();
        
        return ConversationResponse.builder()
                .id(conversation.getId())
                .user1Id(conversation.getUser1().getId())
                .user1Name(conversation.getUser1().getFirstName() + " " + conversation.getUser1().getLastName())
                .user2Id(conversation.getUser2().getId())
                .user2Name(conversation.getUser2().getFirstName() + " " + conversation.getUser2().getLastName())
                .lastMessageTime(conversation.getUpdatedAt())
                .unreadCount(unreadCount)
                .isActive(conversation.getIsActive())
                .build();
    }

    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/"));
    }
}
