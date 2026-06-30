package ch.wiss.ranked.dto.response;

import ch.wiss.ranked.enums.DuelStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class DuelResponse {
    private Long id;
    private String topicTitle;
    private DuelStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String winnerUsername;
    private List<PlayerScoreResponse> players;

    @Data @Builder
    public static class PlayerScoreResponse {
        private Long userId;
        private String username;
        private int score;
    }
}
