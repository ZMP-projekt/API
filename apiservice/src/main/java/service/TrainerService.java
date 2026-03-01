package service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dto.TrainerDTO;
import lombok.RequiredArgsConstructor;
import model.TrainerProfile;
import model.User;
import repository.TrainerProfileRepository;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerProfileRepository trainerRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public void updateProfile(String email, TrainerDTO dto) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

    TrainerProfile profile = trainerRepository.findById(user.getId())
            .orElseGet(() -> {
                TrainerProfile newProfile = new TrainerProfile();
                newProfile.setUser(user);
                return newProfile;
            });

     user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    userRepository.save(user);
    
    profile.setSpecialization(dto.getSpecialization());
    profile.setBio(dto.getBio());
    profile.setPhotoUrl(dto.getPhotoUrl());
    profile.setUpdatedAt(LocalDateTime.now());

    trainerRepository.save(profile);
    auditLogService.logAction(email, "UPDATE_PROFILE", "Zaktualizowano profil trenera");
}

    public TrainerDTO getProfileByEmail(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

    TrainerProfile profile = trainerRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Profil trenera nie istnieje"));

    return new TrainerDTO(
            user.getFirstName(),
            user.getLastName(),
            profile.getSpecialization(),
            profile.getBio(),
            profile.getPhotoUrl()
    );
}
}