package dto;

import lombok.Data;
import model.Role;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private Role role;
}