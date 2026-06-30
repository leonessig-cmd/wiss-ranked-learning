package ch.wiss.ranked.repository;

import ch.wiss.ranked.entity.Duel;
import ch.wiss.ranked.enums.DuelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DuelRepository extends JpaRepository<Duel, Long> {
    List<Duel> findByStatus(DuelStatus status);

    @Query("SELECT d FROM Duel d JOIN d.players p WHERE p.user.id = :userId ORDER BY d.createdAt DESC")
    List<Duel> findByPlayerId(@Param("userId") Long userId);

    @Query("SELECT d FROM Duel d WHERE d.status = 'WAITING' AND d.topic.id = :topicId")
    Optional<Duel> findOpenDuelByTopicId(@Param("topicId") Long topicId);
}
