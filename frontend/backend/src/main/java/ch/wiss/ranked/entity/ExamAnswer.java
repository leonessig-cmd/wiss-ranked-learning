package ch.wiss.ranked.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_answers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ExamAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_attempt_id", nullable = false)
    private ExamAttempt examAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_answer_id", nullable = false)
    private Answer selectedAnswer;

    @Column(nullable = false)
    private boolean correct;
}
