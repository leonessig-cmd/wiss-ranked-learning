package ch.wiss.ranked.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "duel_players")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DuelPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duel_id", nullable = false)
    private Duel duel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private int score = 0;
}
