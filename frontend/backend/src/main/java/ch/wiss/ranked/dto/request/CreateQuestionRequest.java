package ch.wiss.ranked.dto.request;

import ch.wiss.ranked.enums.Difficulty;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CreateQuestionRequest {
    @NotNull private Long topicId;
    @NotBlank private String questionText;
    private Difficulty difficulty = Difficulty.EASY;
    private int points = 10;

    @NotNull @Size(min = 2)
    private List<AnswerOption> answers;

    @Data
    public static class AnswerOption {
        @NotBlank private String answerText;
        private boolean isCorrect;
    }
}
