package dto;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class CreateClassRequest {
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime; 
    private int maxParticipants;
    private boolean isPersonalTraining;
    private Long locationId;
}
