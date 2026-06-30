package ch.wiss.ranked.dto.response;

import lombok.*;

@Data @Builder
public class AnswerFeedbackResponse {
    private Long questionId;
    private Long selectedAnswerId;
    private Long correctAnswerId;
    private boolean correct;
    private int pointsEarned;
    private String explanation;
}
