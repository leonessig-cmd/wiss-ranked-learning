package ch.wiss.ranked.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private int xp = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "rank_points")
    @Builder.Default
    private int totalPoints = 0;

    @Column(name = "avatar", length = 255)
    @Builder.Default
    private String avatar = "images/avatars/default.jpeg";
    

    @Column(name = "ranked_games_played", nullable = false)
    @Builder.Default
    private int rankedGamesPlayed = 0;

    @Column(name = "ranked_wins", nullable = false)
    @Builder.Default
    private int rankedWins = 0;

    @Column(name = "ranked_losses", nullable = false)
    @Builder.Default
    private int rankedLosses = 0;

    @Column(name = "ranked_correct_answers", nullable = false)
    @Builder.Default
    private int rankedCorrectAnswers = 0;

    @Column(name = "ranked_total_answers", nullable = false)
    @Builder.Default
    private int rankedTotalAnswers = 0;

    @Column(name = "one_vs_one_points", nullable = false)
    @Builder.Default
    private int oneVsOnePoints = 0;

    @Column(name = "one_vs_one_games_played", nullable = false)
    @Builder.Default
    private int oneVsOneGamesPlayed = 0;

    @Column(name = "one_vs_one_wins", nullable = false)
    @Builder.Default
    private int oneVsOneWins = 0;

    @Column(name = "one_vs_one_losses", nullable = false)
    @Builder.Default
    private int oneVsOneLosses = 0;

    @Column(name = "one_vs_one_correct_answers", nullable = false)
    @Builder.Default
    private int oneVsOneCorrectAnswers = 0;

    @Column(name = "one_vs_one_total_answers", nullable = false)
    @Builder.Default
    private int oneVsOneTotalAnswers = 0;
    

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
