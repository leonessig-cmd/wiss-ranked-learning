package ch.wiss.ranked.repository;

import ch.wiss.ranked.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {}
