package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Message;
import com.example.Oboe.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySenderOrReceiver(User sender, User receiver);
    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.sent_at DESC")
    List<Message> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    // Lấy users là receivers của current user
    @Query("SELECT u FROM User u JOIN Message m ON " +
            "(m.sender = :user AND m.receiver = u) OR (m.receiver = :user AND m.sender = u) " +
            "WHERE u != :user GROUP BY u")
    List<User> findUsersWithMessages(@Param("user") User user);
}
