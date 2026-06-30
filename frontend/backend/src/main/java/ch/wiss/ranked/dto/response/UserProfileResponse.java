package ch.wiss.ranked.dto.response;

import lombok.*;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String avatar;

    private int totalPoints;
    private String rankName;
    private int leaderboardPosition;
}