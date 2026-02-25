package service;

import dto.TrainerDTO;
import model.TrainerProfile;
import model.User;
import repository.TrainerProfileRepository;
import repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerProfileRepository trainerRepository;
    private final UserRepository userRepository;

    @Transactional
    public void updateProfile(String email, TrainerDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        TrainerProfile profile = trainerRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Profil trenera nie istnieje"));

        // Aktualizujemy dane
        profile.setSpecialization(dto.getSpecialization());
        profile.setBio(dto.getBio());
        profile.setPhotoUrl(dto.getPhotoUrl());
        profile.setUpdatedAt(LocalDateTime.now());

        trainerRepository.save(profile);
    }
}