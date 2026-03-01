package dto;

import lombok.Data;
import model.MembershipType;
import java.time.LocalDateTime;

@Data
public class MembershipDTO {
    private MembershipType type;
    private double price;
    private LocalDateTime endDate;
    private boolean active;
}