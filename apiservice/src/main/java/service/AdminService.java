package service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dto.AdminUserDTO;
import dto.TrainerDTO;
import lombok.RequiredArgsConstructor;
import model.Role;
import model.TrainerProfile;
import model.User;
import repository.TrainerProfileRepository;
import repository.UserRepository;



@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final AuditLogService auditLogService;

    public List<AdminUserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            AdminUserDTO dto = new AdminUserDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
            return dto;
        }).collect(Collectors.toList());
    }


    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        auditLogService.logAction("System", "DELETE_USER", "Usunięto użytkownika o ID: " + userId);
    }


    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setRole(newRole);
        userRepository.save(user);

            if (newRole == Role.ROLE_TRAINER) {
            if (!trainerProfileRepository.existsById(userId)) {
                TrainerProfile profile = new TrainerProfile();
                profile.setUser(user);
            
                profile.setSpecialization("Do uzupełnienia");
                profile.setBio("Brak opisu");
                
                trainerProfileRepository.save(profile);
                auditLogService.logAction("System", "CREATE_TRAINER_PROFILE", "Utworzono profil trenera dla użytkownika o ID: " + userId);
            }
        }

    }

    @Transactional
    public void overrideTrainerData(Long userId, TrainerDTO dto) {
    TrainerProfile profile = trainerProfileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Ten użytkownik nie jest trenerem!"));

    profile.setSpecialization(dto.getSpecialization());
    profile.setBio(dto.getBio());
    profile.setPhotoUrl(dto.getPhotoUrl());
    profile.setUpdatedAt(LocalDateTime.now());

    trainerProfileRepository.save(profile);
    auditLogService.logAction("System", "UPDATE_TRAINER_PROFILE", "Zaktualizowano dane trenera o ID: " + userId);
}


}