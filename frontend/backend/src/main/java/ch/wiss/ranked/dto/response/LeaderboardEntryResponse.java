package ch.wiss.ranked.dto.response;

import lombok.*;

@Data @Builder
public class LeaderboardEntryResponse {
    private int position;
    private Long userId;
    private String username;
    private int totalPoints;
    private String rankName;
}
