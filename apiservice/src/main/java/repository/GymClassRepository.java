package repository;

import model.GymClass;
import model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface GymClassRepository extends JpaRepository<GymClass, Long> {
    List<GymClass> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<GymClass> findByParticipantsContains(User user);
    List<GymClass> findByTrainerAndStartTimeBetween(User trainer, LocalDateTime start, LocalDateTime end);
    List<GymClass> findByLocationId(Long locationId);

    @Query("SELECT COUNT(g) > 0 FROM GymClass g WHERE g.trainer.id = :trainerId " +
       "AND ((g.startTime < :endTime AND g.endTime > :startTime))")
    
    
   boolean existsOverlappingClass(@Param("trainerId") Long trainerId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}