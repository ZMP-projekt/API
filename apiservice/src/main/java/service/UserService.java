package service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import model.User;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO getCurrentUserDto() {
    
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + email));
                  return mapToDTO(user);
    }

    public List<UserDTO> findUsersByFirstName(String name) {
        return userRepository.findByFirstNameContainingIgnoreCase(name)
                .stream().map(this::mapToDTO).toList();
    }

    public List<UserDTO> findUsersByLastName(String name) {
        return userRepository.findByLastNameContainingIgnoreCase(name)
                .stream().map(this::mapToDTO).toList();
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setFirstName(user.getFirstName()); 
        dto.setLastName(user.getLastName());   
        return dto;
    }

    
}