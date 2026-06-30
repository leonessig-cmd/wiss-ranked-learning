package ch.wiss.ranked.dto.response;

import ch.wiss.ranked.enums.Difficulty;
import lombok.*;
import java.util.List;

@Data @Builder
public class QuestionResponse {
    private Long id;
    private String questionText;
    private Difficulty difficulty;
    private int points;
    private List<AnswerOptionResponse> answers;

    @Data @Builder
    public static class AnswerOptionResponse {
        private Long id;
        private String answerText;
    }
}
