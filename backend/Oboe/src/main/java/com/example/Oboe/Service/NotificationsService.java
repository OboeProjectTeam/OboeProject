package com.example.Oboe.Service;

import com.example.Oboe.DTOs.NotificationsDTO;
import com.example.Oboe.Entity.Notifications;
import com.example.Oboe.Repository.MessageRepository;
import com.example.Oboe.Repository.NotificationsRepository;
import com.example.Oboe.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationsService {

    private final UserRepository userRepository;
    private final NotificationsRepository notificationsRepository;

    public NotificationsService( UserRepository userRepository,NotificationsRepository notificationsRepository) {

      this.userRepository = userRepository;
      this.notificationsRepository = notificationsRepository;
    }
    public List<NotificationsDTO> getAllNotification(UUID userId){

        Pageable top30 = PageRequest.of(0, 30); // chỉ lấy 30 thông báo mới nhất
        List<Notifications> notifications = notificationsRepository.findConversation(userId,top30);
        Collections.reverse(notifications);//chuyển tin nhắn từ mới tới cũ
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public int markAllNotificationsAsRead(UUID userId) {
        return notificationsRepository.markAllAsRead(userId);
    }


    public NotificationsDTO convertToDTO(Notifications notifications) {

        return new NotificationsDTO(
                notifications.getNotifiId(),
                notifications.getUser().getUser_id(),
                notifications.getText_notification(),
                notifications.isRead(),
                notifications.getUpdate_at()
        );
    }

}
