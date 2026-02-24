package dto;

import model.Role;
import lombok.Data;

@Data
public class AdminUserDTO {
    private Long id;
    private String email;
    private Role role;
}