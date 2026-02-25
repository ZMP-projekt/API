package model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import model.User;


@Entity
@Data
@Table(name = "trainer_profiles")
public class TrainerProfile {
    @Id
    private Long id; 

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String specialization; //np. fitness, yoga, crossfit, lamanie kregoslupow, trening funkcjonalny
    private String bio;           //np o mnie, doświadczenie, certyfikaty
    private String photoUrl;      
    
    private LocalDateTime updatedAt;
}
