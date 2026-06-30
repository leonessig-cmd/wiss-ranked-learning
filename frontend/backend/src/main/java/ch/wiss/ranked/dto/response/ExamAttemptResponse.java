package ch.wiss.ranked.dto.response;

import ch.wiss.ranked.enums.ExamStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class ExamAttemptResponse {
    private Long id;
    private String examTitle;
    private int score;
    private int maxScore;
    private int percentage;
    private ExamStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private List<AnswerFeedbackResponse> answers;
}
