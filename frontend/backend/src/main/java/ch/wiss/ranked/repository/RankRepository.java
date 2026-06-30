package ch.wiss.ranked.repository;

import ch.wiss.ranked.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Long> {
    @Query("SELECT r FROM Rank r WHERE :points >= r.minPoints AND :points <= r.maxPoints")
    Optional<Rank> findByPoints(@Param("points") int points);
}
