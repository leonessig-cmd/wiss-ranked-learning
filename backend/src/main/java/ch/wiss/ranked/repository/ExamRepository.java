package ch.wiss.ranked.repository;

import ch.wiss.ranked.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {}
