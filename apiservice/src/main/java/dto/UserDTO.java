package dto;

import lombok.Data;
import model.Role;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private Role role;

}