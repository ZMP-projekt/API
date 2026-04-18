package model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;


@Entity
@Data
@Table(name = "gym_locations")
@NoArgsConstructor
@AllArgsConstructor
public class GymLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;      
    private String city;      
    private String address;   
}