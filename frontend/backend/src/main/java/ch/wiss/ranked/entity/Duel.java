package ch.wiss.ranked.entity;

import ch.wiss.ranked.enums.DuelStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "duels")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Duel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private DuelStatus status = DuelStatus.WAITING;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_user_id")
    private User winner;

    @OneToMany(mappedBy = "duel", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<DuelPlayer> players = new ArrayList<>();
}
