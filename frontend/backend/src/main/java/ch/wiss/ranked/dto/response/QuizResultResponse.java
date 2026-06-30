package ch.wiss.ranked.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class QuizResultResponse {
    private Long attemptId;
    private int score;
    private int maxScore;
    private int percentage;
    private LocalDateTime finishedAt;
    private List<AnswerFeedbackResponse> answers;
}
