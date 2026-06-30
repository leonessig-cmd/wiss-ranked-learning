package ch.wiss.ranked.repository;

import ch.wiss.ranked.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(
        value = """
            SELECT q.*
            FROM questions q
            JOIN modules m ON m.id = q.module_id
            JOIN ranks r ON r.id = q.rank_id
            WHERE m.module_number = :moduleNumber
              AND r.name = :rankName
            ORDER BY RAND()
            LIMIT :limit
        """,
        nativeQuery = true
    )
    List<Question> findRandomByModuleAndRank(
            @Param("moduleNumber") String moduleNumber,
            @Param("rankName") String rankName,
            @Param("limit") int limit
    );

    @Query(
        value = """
            SELECT q.*
            FROM questions q
            JOIN modules m ON m.id = q.module_id
            JOIN ranks r ON r.id = q.rank_id
            WHERE m.module_number = '117'
              AND r.name = 'Bronze II'
              AND (:topicId IS NOT NULL OR :topicId IS NULL)
            ORDER BY RAND()
            LIMIT :limit
        """,
        nativeQuery = true
    )
    List<Question> findRandomByTopicId(
            @Param("topicId") Long topicId,
            @Param("limit") int limit
    );

    @Query(
        value = """
            SELECT q.*
            FROM questions q
            JOIN modules m ON m.id = q.module_id
            JOIN ranks r ON r.id = q.rank_id
            WHERE m.module_number = '117'
              AND r.name = 'Bronze II'
              AND (:topicId IS NOT NULL OR :topicId IS NULL)
            ORDER BY q.id
        """,
        nativeQuery = true
    )
    List<Question> findByTopicId(@Param("topicId") Long topicId);
}