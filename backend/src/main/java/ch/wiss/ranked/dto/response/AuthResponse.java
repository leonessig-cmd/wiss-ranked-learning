package ch.wiss.ranked.dto.response;

import lombok.*;

@Data @Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String username;
    private String role;
}
