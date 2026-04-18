package controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import dto.CreateClassRequest;
import dto.GymClassDTO;
import dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import service.GymClassService;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Gym Classes", description = "Zarządzanie grafikiem i zapisami na zajęcia")
public class GymClassController {

    private final GymClassService gymClassService;

    @Operation(summary = "Stwórz nowe zajęcia (Tylko Trener)")
    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<GymClassDTO> createClass(@RequestBody CreateClassRequest request) {
        return ResponseEntity.ok(gymClassService.createClass(request));
    }

    @Operation(summary = "Pobierz grafik na konkretny dzień (Query Param: date)")
    @GetMapping("/by-date")
    public ResponseEntity<List<GymClassDTO>> getClassesByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(gymClassService.getClassesByDate(date));
    }

    @Operation(summary = "Pobierz zajęcia dla konkretnej lokalizacji (Path Variable: locationId)")
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<GymClassDTO>> getClassesByLocation(@PathVariable("locationId") Long locationId) {
        return ResponseEntity.ok(gymClassService.getClassesByLocation(locationId));
    }

    @Operation(summary = "Zapis na zajęcia")
    @PostMapping("/{id}/book")
    public ResponseEntity<String> bookClass(@PathVariable Long id) {
        gymClassService.bookClass(id);
        return ResponseEntity.ok("Pomyślnie zapisano na zajęcia!");
    }

    @Operation(summary = "Anuluj zapis na zajęcia")
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        gymClassService.cancelBooking(id);
        return ResponseEntity.ok("Zrezygnowano z zajęć.");
    }

    @Operation(summary = "Przełóż zajęcia (Tylko Trener)")
    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> reschedule(@PathVariable Long id, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newTime) {
        gymClassService.rescheduleClass(id, newTime);
        return ResponseEntity.ok("Zajęcia zostały przełożone.");
    }

    @Operation(summary = "Usuń zajęcia (Tylko Trener)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        gymClassService.deleteClass(id);
        return ResponseEntity.ok("Zajęcia zostały odwołane.");
    }

    @GetMapping("/trainer")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "Mój grafik (Trener)")
    public ResponseEntity<List<GymClassDTO>> getMyClasses(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(gymClassService.getTrainerClassesForDay(date));
    }

    @GetMapping("/{id}/participants")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "Lista zapisanych osób")
    public ResponseEntity<List<UserDTO>> getParticipants(@PathVariable("id") Long id) {
        return ResponseEntity.ok(gymClassService.getParticipants(id));
    }
}