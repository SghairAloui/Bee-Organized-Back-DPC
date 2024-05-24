package com.BeeOranized.BeeOranized.Repository;

import com.BeeOranized.BeeOranized.Dtos.ConversationResponse;
import com.BeeOranized.BeeOranized.Entity.Conversation;
import com.BeeOranized.BeeOranized.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Optional<Conversation> findConversationByUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query(
            nativeQuery = true,
            value = """
    SELECT
        C.conversation_id AS conversationId,
        U.user_id AS otherUserId,
        U.name AS otherUserName,
        M.message AS lastMessage,
        M.lastMessageTimestamp AS lastMessageTimestamp
    FROM conversation AS C
    INNER JOIN users AS U
        ON (C.user1_id = U.user_id OR C.user2_id = U.user_id) AND U.user_id != :userId
    LEFT JOIN (
        SELECT
            conversation_id,
            message,
            timestamp AS lastMessageTimestamp
        FROM message
        WHERE (conversation_id, timestamp) IN (
            SELECT conversation_id, MAX(timestamp) AS timestamp
            FROM message
            GROUP BY conversation_id
        )
    ) AS M
    ON C.conversation_id = M.conversation_id
    WHERE C.user1_id = :userId OR C.user2_id = :userId
    ORDER BY M.lastMessageTimestamp DESC;
"""
    )
    List<ConversationResponse> findConversationsByUserId(@Param("userId") int userId);
}
