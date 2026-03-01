package service;

import dto.AuditLogDTO;
import lombok.RequiredArgsConstructor;
import model.AuditLog;
import repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

   
    public void logAction(String email, String action, String details) {
        AuditLog log = new AuditLog(null, email, action, details, LocalDateTime.now());
        auditLogRepository.save(log);
    }

   
    public List<AuditLogDTO> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream().map(log -> {
            AuditLogDTO dto = new AuditLogDTO();
            dto.setChangedBy(log.getChangedBy());
            dto.setAction(log.getAction());
            dto.setDetails(log.getDetails());
            dto.setTimestamp(log.getTimestamp());
            return dto;
        }).collect(Collectors.toList());
    }
}