package ch.wiss.ranked.repository;

import ch.wiss.ranked.entity.DuelPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DuelPlayerRepository extends JpaRepository<DuelPlayer, Long> {
    Optional<DuelPlayer> findByDuelIdAndUserId(Long duelId, Long userId);
}
