package dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerDTO {
    private String firstName;
    private String lastName;
    private String specialization;
    private String bio;
    private String photoUrl;
}