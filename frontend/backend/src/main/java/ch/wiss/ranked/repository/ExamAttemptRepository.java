package ch.wiss.ranked.repository;

import ch.wiss.ranked.entity.ExamAttempt;
import ch.wiss.ranked.enums.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByUserIdOrderByStartedAtDesc(Long userId);
    Optional<ExamAttempt> findByUserIdAndExamIdAndStatus(Long userId, Long examId, ExamStatus status);
}
