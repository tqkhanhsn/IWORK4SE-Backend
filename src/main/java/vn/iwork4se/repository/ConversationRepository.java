package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.Conversation;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c WHERE " +
            "((c.user1.id = :userId1 AND c.user2.id = :userId2) OR " +
            "(c.user1.id = :userId2 AND c.user2.id = :userId1)) " +
            "AND c.isActive = true " +
            "ORDER BY c.updatedAt DESC")
    List<Conversation> findActiveConversationsBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);
    
    @Query("SELECT c FROM Conversation c WHERE " +
            "(c.user1.id = :userId1 AND c.user2.id = :userId2) OR " +
            "(c.user1.id = :userId2 AND c.user2.id = :userId1)")
    List<Conversation> findAllConversationsBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Query("SELECT c FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId ORDER BY c.updatedAt DESC")
    Page<Conversation> findConversationsByUser(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE (c.user1.id = :userId OR c.user2.id = :userId) AND c.isActive = true ORDER BY c.updatedAt DESC")
    List<Conversation> findActiveConversationsByUser(@Param("userId") String userId);
}
