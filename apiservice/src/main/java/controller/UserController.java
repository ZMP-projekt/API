package controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dto.UserDTO;
import service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

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

  @GetMapping("/test-auth")
public String testAuth(Authentication auth) {
    if (auth == null) return "Brak tokenu!";
    return "Zalogowany jako: " + auth.getName() + ", Twoja rola: " + auth.getAuthorities().toString();
}

@GetMapping("/search/first-name")
    public ResponseEntity<List<UserDTO>> searchByFirstName(@RequestParam String name) {
        return ResponseEntity.ok(userService.findUsersByFirstName(name));
    }

    @GetMapping("/search/last-name")
    public ResponseEntity<List<UserDTO>> searchByLastName(@RequestParam String name) {
        return ResponseEntity.ok(userService.findUsersByLastName(name));
    }

}
