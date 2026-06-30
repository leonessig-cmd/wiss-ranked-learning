package ch.wiss.ranked.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ranks")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Rank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "min_points", nullable = false)
    private int minPoints;

    @Column(name = "max_points", nullable = false)
    private int maxPoints;
}
