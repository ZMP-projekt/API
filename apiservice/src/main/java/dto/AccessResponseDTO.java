package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessResponseDTO {
    private boolean accessGranted;
    private String message;
}