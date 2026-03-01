package dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogDTO {
    private String changedBy;
    private String action;
    private String details;
    private LocalDateTime timestamp;
}