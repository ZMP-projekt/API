package dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GymClassDTO {
    private Long id;
    private String name;
    private String trainerName; 
    private LocalDateTime startTime;
    private int currentParticipants;
    private int maxParticipants;
    private boolean isUserEnrolled; 
    private String description;
    private LocalDateTime endTime;
    private boolean isPersonalTraining;
}