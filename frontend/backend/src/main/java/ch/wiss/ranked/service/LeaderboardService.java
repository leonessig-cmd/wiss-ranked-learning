package ch.wiss.ranked.service;

import ch.wiss.ranked.dto.response.LeaderboardEntryResponse;
import ch.wiss.ranked.dto.response.UserProfileResponse;
import ch.wiss.ranked.entity.User;
import ch.wiss.ranked.exception.ResourceNotFoundException;
import ch.wiss.ranked.repository.RankRepository;
import ch.wiss.ranked.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepo;
    private final RankRepository rankRepo;
    private final XpService xpService;

    public List<LeaderboardEntryResponse> getLeaderboard() {
        List<User> users = userRepo.findLeaderboard();
        List<LeaderboardEntryResponse> result = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            String rankName = rankRepo.findByPoints(u.getTotalPoints())
                    .map(r -> r.getName()).orElse("Unbewertet");
            result.add(LeaderboardEntryResponse.builder()
                    .position(i + 1)
                    .userId(u.getId())
                    .username(u.getUsername())
                    .totalPoints(u.getTotalPoints())
                    .rankName(rankName)
                    .build());
        }
        return result;
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User nicht gefunden"));

        List<User> allUsers = userRepo.findLeaderboard();
        int position = 1;
        for (User u : allUsers) {
            if (u.getId().equals(userId)) break;
            position++;
        }

        String rankName = rankRepo.findByPoints(user.getTotalPoints())
                .map(r -> r.getName()).orElse("Unbewertet");

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .totalPoints(user.getTotalPoints())
                .rankName(rankName)
                .leaderboardPosition(position)
                .build();
    }
}
