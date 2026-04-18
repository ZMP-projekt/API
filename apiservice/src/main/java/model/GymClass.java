package model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "gym_classes")
public class GymClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private User trainer; 

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxParticipants;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private GymLocation location;
    
    @ManyToMany
    @JoinTable(
        name = "class_enrollments",
        joinColumns = @JoinColumn(name = "class_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();
    private boolean isPersonalTraining; 

}