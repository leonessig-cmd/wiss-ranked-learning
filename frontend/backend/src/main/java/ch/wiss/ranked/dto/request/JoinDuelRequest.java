package ch.wiss.ranked.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinDuelRequest {
    @NotNull private Long topicId;
}
