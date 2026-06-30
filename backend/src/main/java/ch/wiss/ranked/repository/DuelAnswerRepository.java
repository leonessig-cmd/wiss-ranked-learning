package ch.wiss.ranked.repository;

import ch.wiss.ranked.entity.DuelAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DuelAnswerRepository extends JpaRepository<DuelAnswer, Long> {
    List<DuelAnswer> findByDuelIdAndUserId(Long duelId, Long userId);
    boolean existsByDuelIdAndUserIdAndQuestionId(Long duelId, Long userId, Long questionId);
    long countByDuelId(Long duelId);
}
