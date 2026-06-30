package ch.wiss.ranked.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAnswerRequest {
    @NotNull private Long questionId;
    @NotNull private Long selectedAnswerId;
}
