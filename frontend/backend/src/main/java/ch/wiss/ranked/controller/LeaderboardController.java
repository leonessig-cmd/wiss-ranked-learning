package ch.wiss.ranked.controller;

import ch.wiss.ranked.dto.response.LeaderboardEntryResponse;
import ch.wiss.ranked.dto.response.UserProfileResponse;
import ch.wiss.ranked.entity.User;
import ch.wiss.ranked.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getLeaderboard());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(leaderboardService.getUserProfile(user.getId()));
    }

    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(leaderboardService.getUserProfile(userId));
    }
}
