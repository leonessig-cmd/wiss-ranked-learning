package ch.wiss.ranked.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateExamRequest {
    @NotBlank private String title;
    private String description;
    private int durationMinutes = 30;

    @NotNull private List<Long> questionIds;
}
