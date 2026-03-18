package br.com.gubee.interview.core.infrastructure.persistence.entities;
import br.com.gubee.interview.model.domain.entities.Hero;
import br.com.gubee.interview.model.domain.enums.Race;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hero")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeroEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Race race;

    @Column(name = "power_stats_id", nullable = false)
    private UUID powerStatsId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean enabled;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();  // ADICIONADO - updated_at não pode ser nulo
        this.enabled = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public static HeroEntity fromDomain(Hero hero) {
        return HeroEntity.builder()
                .id(hero.getId() != null ? hero.getId() : UUID.randomUUID())
                .name(hero.getName())
                .race(hero.getRace())
                .powerStatsId(hero.getPowerStatsId())
                .createdAt(hero.getCreatedAt() != null ? hero.getCreatedAt() : Instant.now())
                .updatedAt(Instant.now())
                .enabled(hero.isEnabled())
                .build();
    }

    public Hero toDomain() {
        return Hero.builder()
                .id(id)
                .name(name)
                .race(race)
                .powerStatsId(powerStatsId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .enabled(enabled)
                .build();
    }
}