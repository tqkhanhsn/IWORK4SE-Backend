package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt DESC")
    Page<Message> findMessagesByConversation(@Param("conversationId") Long conversationId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.isRead = false AND m.receiver.id = :userId")
    List<Message> findUnreadMessagesByConversation(@Param("conversationId") Long conversationId, @Param("userId") String userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    Long countUnreadMessages(@Param("userId") String userId);
}
