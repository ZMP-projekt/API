package controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.TrainerDTO;
import lombok.RequiredArgsConstructor;
import repository.TrainerProfileRepository;
import service.TrainerService;

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

    @PutMapping("/me")
    @PreAuthorize("hasRole('TRAINER')")
    public void updateMyProfile(@RequestBody TrainerDTO dto, Authentication auth) {
        trainerService.updateProfile(auth.getName(), dto);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainerDTO> getMyProfile(Authentication auth) {
    return ResponseEntity.ok(trainerService.getProfileByEmail(auth.getName()));
}
}