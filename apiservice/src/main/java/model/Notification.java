package model;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Do kogo skierowane jest powiadomienie

    private String content; // Treść: "Mikołaj zapisał się na zajęcia..."
    
    private LocalDateTime createdAt;
    
    private boolean isRead = false;
}