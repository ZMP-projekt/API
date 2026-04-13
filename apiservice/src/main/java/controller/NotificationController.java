package controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import model.Notification;
import repository.NotificationRepository;
import dto.NotificationDTO;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "System powiadomień dla użytkowników")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    @Operation(summary = "Pobierz moje powiadomienia")
    public ResponseEntity<List<NotificationDTO>> getMyNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<NotificationDTO> dtos = notificationRepository.findByUserEmailOrderByCreatedAtDesc(email)
            .stream()
            .map(n -> new NotificationDTO(n.getId(), n.getContent(), n.getCreatedAt(), n.isRead()))
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Oznacz powiadomienie jako przeczytane")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono powiadomienia"));
        
        // Zabezpieczenie: sprawdź, czy to na pewno powiadomienie zalogowanego użytkownika
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!notification.getUser().getEmail().equals(email)) {
            return ResponseEntity.status(403).build();
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń powiadomienie")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}