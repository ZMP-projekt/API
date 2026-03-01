package service;

import dto.AccessResponseDTO;
import dto.MembershipDTO;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import model.*;
import repository.MembershipRepository;
import repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public void purchaseMembership(String email, MembershipType type) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik o emailu " + email + " nie istnieje w bazie."));

        double price = switch (type) {
            case OPEN -> 170.0;
            case NIGHT -> 80.0;
            case STUDENT -> 100.0; 
        };

        Membership membership = membershipRepository.findByUserId(user.getId())
                .orElse(new Membership());

        membership.setUser(user);
        membership.setType(type);
        membership.setStartDate(LocalDateTime.now());
        membership.setEndDate(LocalDateTime.now().plusMonths(1));
        membership.setActive(true);

        membershipRepository.save(membership);

        auditLogService.logAction(email, "PURCHASE_MEMBERSHIP", 
            String.format("Zakupiono karnet %s za %.2f zł", type, price));
    }

    public MembershipDTO getUserMembership(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return membershipRepository.findByUserId(user.getId())
                .map(m -> {
                    MembershipDTO dto = new MembershipDTO();
                    dto.setType(m.getType());
                    dto.setEndDate(m.getEndDate());
                    dto.setActive(m.isActive());
                    return dto;
                }).orElseThrow(() -> new ResourceNotFoundException("Ten użytkownik nie posiada jeszcze żadnego karnetu."));
    }


        public AccessResponseDTO checkAccess(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        Membership membership = membershipRepository.findByUserId(user.getId())
                .orElse(null);

        if (membership == null || !membership.isActive()) {
            return new AccessResponseDTO(false, "Brak aktywnego karnetu.");
        }

        if (membership.getEndDate().isBefore(LocalDateTime.now())) {
            membership.setActive(false); 
            membershipRepository.save(membership);
            return new AccessResponseDTO(false, "Karnet wygasł.");
        }

        if (membership.getType() == MembershipType.NIGHT) {
            LocalTime now = LocalTime.now();
            boolean isNightTime = now.isAfter(LocalTime.of(22, 0)) || now.isBefore(LocalTime.of(6, 0));
            
            if (!isNightTime) {
                return new AccessResponseDTO(false, "Karnet NIGHT obowiązuje tylko w godzinach 22:00 - 06:00.");
            }
        }

        return new AccessResponseDTO(true, "Dostęp przyznany. Witamy na siłowni.");
    }
}