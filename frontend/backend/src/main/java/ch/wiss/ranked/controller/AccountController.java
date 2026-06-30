package ch.wiss.ranked.controller;

import ch.wiss.ranked.entity.User;
import ch.wiss.ranked.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PutMapping
    public ResponseEntity<?> updateAccount(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body
    ) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Nicht eingeloggt"));
        }

        User dbUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User nicht gefunden"));

        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        String avatar = body.get("avatar");

        if (username != null && !username.isBlank()) {
            dbUser.setUsername(username);
        }

        if (email != null && !email.isBlank()) {
            dbUser.setEmail(email);
        }

        if (password != null && !password.isBlank()) {
            dbUser.setPassword(passwordEncoder.encode(password));
        }

        if (avatar != null && !avatar.isBlank()) {
            dbUser.setAvatar(avatar);
        }

        userRepository.save(dbUser);

        return ResponseEntity.ok(Map.of(
                "message", "Account gespeichert",
                "username", dbUser.getUsername(),
                "email", dbUser.getEmail(),
                "avatar", dbUser.getAvatar()
        ));
    }
}