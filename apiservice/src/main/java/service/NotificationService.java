package service;

import java.time.LocalDateTime;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import repository.NotificationRepository;
import model.Notification;
import model.User;


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

        // 2. Wysłanie "na żywo" przez WebSocket
        // Marcin będzie słuchał na: /user/queue/notifications
        messagingTemplate.convertAndSendToUser(
                user.getEmail(), 
                "/queue/notifications", 
                message
        );
    }
}