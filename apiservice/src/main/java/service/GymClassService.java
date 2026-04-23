package service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dto.CreateClassRequest;
import dto.GymClassDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import model.GymClass;
import model.GymLocation;
import model.User;
import repository.GymClassRepository;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GymClassService {

    private final GymClassRepository gymClassRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final LocationService locationService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<GymClassDTO> getClassesByDate(LocalDateTime start) {
        LocalDateTime end = start.plusDays(1);
        List<GymClass> classes = gymClassRepository.findByStartTimeBetween(start, end);
        User currentUser = getCurrentUser();

        return classes.stream()
                .map(gymClass -> mapToDTO(gymClass, currentUser))
                .toList();
    }

    public List<GymClassDTO> getClassesByLocation(Long locationId) {
        List<GymClass> classes = gymClassRepository.findByLocationId(locationId);
        User currentUser = getCurrentUser();

        return classes.stream()
                .map(gymClass -> mapToDTO(gymClass, currentUser))
                .toList();
    }

    public void bookClass(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zajęć o ID: " + classId));

        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Użytkownik nie istnieje");

        if (gymClass.getParticipants().size() >= gymClass.getMaxParticipants()) {
            throw new RuntimeException("Brak wolnych miejsc!");
        }

        if (gymClass.getParticipants().contains(user)) {
            throw new RuntimeException("Jesteś już zapisany.");
        }

        gymClass.getParticipants().add(user);
        gymClassRepository.save(gymClass);

        User trainer = gymClass.getTrainer();
        if (trainer != null) {
            notificationService.sendToUser(trainer, "Nowy uczestnik: " + user.getFirstName() + " na " + gymClass.getName());
        }
        
        auditLogService.logAction(user.getEmail(), "BOOKING", "Zapis na: " + gymClass.getName());
    }

    public GymClassDTO createClass(CreateClassRequest request) {
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Nie można utworzyć zajęć w przeszłości!");
        }

        User trainer = getCurrentUser();
        if (trainer == null) throw new RuntimeException("Nie znaleziono trenera");

        LocalDateTime endTime = (request.getEndTime() != null) ? request.getEndTime() : request.getStartTime().plusHours(1);
                
        if (gymClassRepository.existsOverlappingClass(trainer.getId(), request.getStartTime(), endTime)) {
            throw new RuntimeException("Masz już inne zajęcia w tym czasie!");
        }
      
        GymLocation location = locationService.getLocationById(request.getLocationId());
        GymClass newClass = new GymClass();

        newClass.setName(request.getName());
        newClass.setDescription(request.getDescription());
        newClass.setStartTime(request.getStartTime());
        newClass.setEndTime(endTime);
        newClass.setTrainer(trainer);
        newClass.setLocation(location);
        newClass.setPersonalTraining(request.isPersonalTraining());
        newClass.setMaxParticipants(request.isPersonalTraining() ? 1 : request.getMaxParticipants());

        GymClass savedClass = gymClassRepository.save(newClass);
        auditLogService.logAction(trainer.getEmail(), "CREATE_CLASS", "Zajęcia: " + savedClass.getName() + " w: " + location.getName());
        return mapToDTO(savedClass, trainer);
    }

    public void cancelBooking(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId).orElseThrow();
        User user = getCurrentUser();

        if (!gymClass.getParticipants().contains(user)) {
            throw new RuntimeException("Nie jesteś zapisany.");
        }

        gymClass.getParticipants().remove(user);
        gymClassRepository.save(gymClass);
        auditLogService.logAction(user.getEmail(), "CANCEL_BOOKING", "Anulowano: " + gymClass.getName());

        if (gymClass.getTrainer() != null) {
            notificationService.sendToUser(gymClass.getTrainer(), user.getFirstName() + " zrezygnował z " + gymClass.getName());
        }
    }

    public void rescheduleClass(Long classId, LocalDateTime newTime) {
        GymClass gymClass = gymClassRepository.findById(classId).orElseThrow();
        validateTrainer(gymClass);

        LocalDateTime oldTime = gymClass.getStartTime();
        long duration = java.time.Duration.between(gymClass.getStartTime(), gymClass.getEndTime()).toMinutes();
        
        gymClass.setStartTime(newTime);
        gymClass.setEndTime(newTime.plusMinutes(duration));
        gymClassRepository.save(gymClass);

        String msg = "Zajęcia " + gymClass.getName() + " przełożone z " + oldTime + " na " + newTime;
        gymClass.getParticipants().forEach(p -> notificationService.sendToUser(p, msg));
        
        auditLogService.logAction(getCurrentUser().getEmail(), "RESCHEDULE", "ID: " + classId + " na " + newTime);
    }

    public List<GymClassDTO> getTrainerClassesForDay(LocalDateTime date) {
        User trainer = getCurrentUser();
        LocalDateTime start = date.toLocalDate().atStartOfDay();
        LocalDateTime end = date.toLocalDate().atTime(23, 59, 59);
        
        return gymClassRepository.findByTrainerAndStartTimeBetween(trainer, start, end)
                .stream()
                .map(gc -> mapToDTO(gc, trainer))
                .toList();
    }

    public List<UserDTO> getParticipants(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId).orElseThrow();
        validateTrainer(gymClass);
        return gymClass.getParticipants().stream()
            .map(u -> {
                UserDTO dto = new UserDTO(); 
                dto.setId(u.getId());       
                dto.setFirstName(u.getFirstName());
                dto.setLastName(u.getLastName());
                dto.setEmail(u.getEmail());
                return dto;
            })
            .collect(Collectors.toList());
    }

    private void validateTrainer(GymClass gymClass) {
        User user = getCurrentUser();
        if (!gymClass.getTrainer().equals(user)) {
            throw new RuntimeException("Brak uprawnień.");
        }
    }

    public void deleteClass(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId).orElseThrow();
        validateTrainer(gymClass);

        String msg = "Zajęcia " + gymClass.getName() + " zostały odwołane.";
        gymClass.getParticipants().forEach(p -> notificationService.sendToUser(p, msg));

        gymClassRepository.delete(gymClass);
    }

    private GymClassDTO mapToDTO(GymClass gymClass, User currentUser) {
        GymClassDTO dto = new GymClassDTO();
        dto.setId(gymClass.getId());
        dto.setName(gymClass.getName());
        dto.setDescription(gymClass.getDescription());
        dto.setStartTime(gymClass.getStartTime());
        dto.setEndTime(gymClass.getEndTime());
        dto.setMaxParticipants(gymClass.getMaxParticipants());
        dto.setCurrentParticipants(gymClass.getParticipants().size());
        dto.setPersonalTraining(gymClass.isPersonalTraining());
        

        if (gymClass.getTrainer() != null) {
            dto.setTrainerName(gymClass.getTrainer().getFirstName() + " " + gymClass.getTrainer().getLastName());
        }

        if (currentUser != null) {
            dto.setUserEnrolled(gymClass.getParticipants().contains(currentUser));
        }
        
        if (gymClass.getLocation() != null) {
            dto.setLocationName(gymClass.getLocation().getName());
            dto.setAddress(gymClass.getLocation().getAddress());
            dto.setCity(gymClass.getLocation().getCity());
            dto.setLatitude(gymClass.getLocation().getLatitude());
            dto.setLongitude(gymClass.getLocation().getLongitude());
        }

        return dto;
    }
}