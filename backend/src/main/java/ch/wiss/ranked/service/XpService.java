package ch.wiss.ranked.service;

import ch.wiss.ranked.entity.User;
import ch.wiss.ranked.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class XpService {

    private final UserRepository userRepository;

    private static final int XP_PER_LEVEL = 100;

    public int calculateLevel(int xp) {
        return (xp / XP_PER_LEVEL) + 1;
    }

    public int getCurrentLevelXp(int xp) {
        return xp % XP_PER_LEVEL;
    }

    public int getXpToNextLevel() {
        return XP_PER_LEVEL;
    }

    public User addXp(User user, int amount) {
        user.setXp(user.getXp() + amount);
        return userRepository.save(user);
    }
}