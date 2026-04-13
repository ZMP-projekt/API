package service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dto.CreateClassRequest;
import dto.GymClassDTO;
import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import model.GymClass;
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

    public List<GymClassDTO> getAllClassesForDay(LocalDateTime start) {
        LocalDateTime end = start.plusDays(1);
        List<GymClass> classes = gymClassRepository.findByStartTimeBetween(start, end);
        
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail).orElse(null);

        return classes.stream()
                .map(gymClass -> mapToDTO(gymClass, currentUser))
                .toList();
    }

    public void bookClass(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zajęć o ID: " + classId));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje"));

        if (gymClass.getParticipants().size() >= gymClass.getMaxParticipants()) {
            throw new RuntimeException("Brak wolnych miejsc na te zajęcia!");
        }

        if (gymClass.getParticipants().contains(user)) {
            throw new RuntimeException("Jesteś już zapisany na te zajęcia");
        }

        gymClass.getParticipants().add(user);
        gymClassRepository.save(gymClass);

        User trainer = gymClass.getTrainer();
        if (trainer != null) {
            String msg = "Nowy uczestnik: " + user.getFirstName() + " zapisał się na " + gymClass.getName();
            notificationService.sendToUser(trainer, msg);
        }
        
        auditLogService.logAction(user.getEmail(), "BOOKING", "Użytkownik zapisał się na zajęcia: " + gymClass.getName());
    } 

    public GymClassDTO createClass(CreateClassRequest request) {
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Nie można utworzyć zajęć w przeszłości!");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User trainer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono trenera w bazie"));

        LocalDateTime endTime = (request.getEndTime() != null) 
                ? request.getEndTime() 
                : request.getStartTime().plusHours(1);
                
        boolean isOverlapping = gymClassRepository.existsOverlappingClass(
                trainer.getId(), request.getStartTime(), endTime);

        if (isOverlapping) {
            throw new RuntimeException("Masz już inne zajęcia w tym czasie!");
        }

        GymClass newClass = new GymClass();
        newClass.setName(request.getName());
        newClass.setDescription(request.getDescription());
        newClass.setStartTime(request.getStartTime());
        newClass.setTrainer(trainer);
        newClass.setPersonalTraining(request.isPersonalTraining());
        newClass.setEndTime(endTime);

        if (request.isPersonalTraining()) {
            newClass.setMaxParticipants(1); 
        } else {
            newClass.setMaxParticipants(request.getMaxParticipants());
        }

        GymClass savedClass = gymClassRepository.save(newClass);
        return mapToDTO(savedClass, trainer);
    }

    public void cancelBooking(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zajęć"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (!gymClass.getParticipants().contains(user)) {
            throw new RuntimeException("Nie jesteś zapisany na te zajęcia");
        }

        gymClass.getParticipants().remove(user);
        gymClassRepository.save(gymClass);
        auditLogService.logAction(user.getEmail(), "CANCEL_BOOKING", "Użytkownik anulował rezerwację na zajęcia: " + gymClass.getName());

    }

    public void rescheduleClass(Long classId, LocalDateTime newTime) {
        GymClass gymClass = gymClassRepository.findById(classId).orElseThrow();
        validateTrainer(gymClass);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        long durationInMinutes = java.time.Duration.between(gymClass.getStartTime(), gymClass.getEndTime()).toMinutes();
        gymClass.setStartTime(newTime);
        gymClass.setEndTime(newTime.plusMinutes(durationInMinutes));
        gymClassRepository.save(gymClass);
        auditLogService.logAction(email, "RESCHEDULE", "Zmieniono termin zajęć ID: " + classId + " na " + newTime);
    }

    public List<GymClassDTO> getTrainerClassesForDay(LocalDateTime date) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User trainer = userRepository.findByEmail(email).orElseThrow();
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
        return gymClassRepository.findByTrainerAndStartTimeBetween(trainer, startOfDay, endOfDay)
                .stream()
                .map(gymClass -> mapToDTO(gymClass, trainer))
                .toList();
    }

    public List<UserDTO> getParticipants(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zajęć"));
        validateTrainer(gymClass);

        return gymClass.getParticipants().stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getId());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setEmail(user.getEmail());
                    return dto;
                })
                .toList();
    }

    private void validateTrainer(GymClass gymClass) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (!gymClass.getTrainer().equals(user)) {
            throw new RuntimeException("Nie masz uprawnień do zmiany tego zajęcia");
        }
    }

    public void deleteClass(Long classId) {
        GymClass gymClass = gymClassRepository.findById(classId).orElseThrow();
        validateTrainer(gymClass);
        gymClassRepository.delete(gymClass);
    }

    private GymClassDTO mapToDTO(GymClass gymClass, User currentUser) {
        GymClassDTO dto = new GymClassDTO();
        dto.setPersonalTraining(gymClass.isPersonalTraining());
        dto.setDescription(gymClass.getDescription());
        dto.setEndTime(gymClass.getEndTime());
        dto.setId(gymClass.getId());
        dto.setName(gymClass.getName());
        dto.setStartTime(gymClass.getStartTime());
        dto.setMaxParticipants(gymClass.getMaxParticipants());
        dto.setCurrentParticipants(gymClass.getParticipants().size());

        if (gymClass.getTrainer() != null) {
            dto.setTrainerName(gymClass.getTrainer().getFirstName() + " " + gymClass.getTrainer().getLastName());
        }

        if (currentUser != null) {
            dto.setUserEnrolled(gymClass.getParticipants().contains(currentUser));
        }

        return dto;
    }
}