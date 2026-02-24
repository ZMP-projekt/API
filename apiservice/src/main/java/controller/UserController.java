package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dto.UserDTO;
import service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpointy do zarządzania użytkownikami")
public class UserController {
    
private final UserService userService;

@GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
       
        return ResponseEntity.ok(userService.getCurrentUserDto());
    }

}
