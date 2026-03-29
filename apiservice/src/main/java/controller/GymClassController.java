package controller;

import dto.CreateClassRequest;
import dto.GymClassDTO;
import service.GymClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @Operation(summary = "Pobierz grafik na konkretny dzień - format taki: 2026-03-29T00:00:00")
    @GetMapping
    public ResponseEntity<List<GymClassDTO>> getClasses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(gymClassService.getAllClassesForDay(date));
    }

    @Operation(summary = "Zapis na zajecia - jako user!")
    @PostMapping("/{id}/book")
    public ResponseEntity<String> bookClass(@PathVariable Long id) {
        gymClassService.bookClass(id);
        return ResponseEntity.ok("Pomyślnie zapisano na zajęcia!");
    }
    @Operation(summary = "Anuluj zapis na zajecia - jako user!")
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
    gymClassService.cancelBooking(id);
    return ResponseEntity.ok("Zrezygnowano z zajęć.");
}
    @Operation(summary = "przeloz zajecia jako trener") 
    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> reschedule(@PathVariable Long id, @RequestParam LocalDateTime newTime) {
    gymClassService.rescheduleClass(id, newTime);
    return ResponseEntity.ok("Zajęcia zostały przełożone.");
}
    @Operation(summary = "usun zajecia jako trener - bo np. silownia sie spalila albo zlamales kregoslup")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
    gymClassService.deleteClass(id);
    return ResponseEntity.ok("Zajęcia zostały odwołane.");
}


}