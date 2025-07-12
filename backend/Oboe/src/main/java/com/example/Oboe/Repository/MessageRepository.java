package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Message;
import com.example.Oboe.Entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query(value = """
    SELECT DISTINCT 
        CASE 
            WHEN m.sender.user_id = :userId THEN m.receiver.user_id
            ELSE m.sender.user_id
        END
    FROM Message m
    WHERE m.sender.user_id = :userId OR m.receiver.user_id = :userId
    """)
    List<UUID> findAllPartnerIds(@Param("userId") UUID userId);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.user_id = :userA AND m.receiver.user_id = :userB) OR " +
            "(m.sender.user_id = :userB AND m.receiver.user_id = :userA) " +
            "ORDER BY m.sent_at DESC")
    List<Message> findConversation(@Param("userA") UUID userA,
                                         @Param("userB") UUID userB,
                                         org.springframework.data.domain.Pageable pageable);
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.user_id = :userA AND m.receiver.user_id = :userB) OR " +
            "(m.sender.user_id = :userB AND m.receiver.user_id = :userA) " +
            "ORDER BY m.sent_at DESC")
    List<Message> findMessageNew(@Param("userA") UUID userA,
                                           @Param("userB") UUID userB,
                                           Pageable pageable);





}
