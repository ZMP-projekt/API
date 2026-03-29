package dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateClassRequest {
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime; // Opcjonalne, chyba do zabeczpieczenia przed błędami sie przyda
    private int maxParticipants;
    private boolean isPersonalTraining;
}
