package service;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import model.Notification;
import model.User;
import repository.NotificationRepository;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    public void sendToUser(User user, String message) {
        // 1. Zapis do bazy danych (historia)
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setContent(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);

        NotificationDTO dto = new NotificationDTO(
        notification.getId(),
        notification.getContent(),
        notification.getCreatedAt(),
        notification.isRead()
    );

    messagingTemplate.convertAndSendToUser(
        user.getEmail(), 
        "/queue/notifications", 
        dto
    );
    
    }
}