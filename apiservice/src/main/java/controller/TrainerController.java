package controller;

import dto.TrainerDTO;
import model.TrainerProfile;
import repository.TrainerProfileRepository;
import service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerProfileRepository trainerRepository;
    private final TrainerService trainerService;

    // Pobiera listę wszystkich trenerów (Dostęp: Publiczny)
    @GetMapping
    public List<TrainerDTO> getAllTrainers() {
        return trainerRepository.findAll().stream()
                .map(p -> new TrainerDTO(
                    p.getUser().getFirstName(), 
                    p.getUser().getLastName(), 
                    p.getSpecialization(), 
                    p.getBio(), 
                    p.getPhotoUrl()))
                .toList();
    }

    // Pozwala trenerowi edytować swój profil (Dostęp: Trener)
    @PutMapping("/me")
    @PreAuthorize("hasRole('TRAINER')")
    public void updateMyProfile(@RequestBody TrainerDTO dto, Authentication auth) {
        trainerService.updateProfile(auth.getName(), dto);
    }
}