package dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import model.Role;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email nie może być pusty")
    private String email;
    @NotBlank(message = "Hasło nie może być puste")
    private String password;
    private Role role;
    @NotBlank(message = "Imię nie może być puste")
    private String firstName;
    @NotBlank(message = "Nazwisko nie może być puste")
    private String lastName;

}