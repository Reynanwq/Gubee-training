package br.com.gubee.interview.core.infrastructure.persistence.entities;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "power_stats")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PowerStatsEntity {

    @Id
    private UUID id;

    @Column(nullable = false, columnDefinition = "SMALLINT")
    private short strength;

    @Column(nullable = false, columnDefinition = "SMALLINT")
    private short agility;

    @Column(nullable = false, columnDefinition = "SMALLINT")
    private short dexterity;

    @Column(nullable = false, columnDefinition = "SMALLINT")
    private short intelligence;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();  // ADICIONADO - updated_at não pode ser nulo
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public static PowerStatsEntity fromDomain(PowerStats powerStats) {
        return PowerStatsEntity.builder()
                .id(powerStats.getId() != null ? powerStats.getId() : UUID.randomUUID())
                .strength((short) powerStats.getStrength())
                .agility((short) powerStats.getAgility())
                .dexterity((short) powerStats.getDexterity())
                .intelligence((short) powerStats.getIntelligence())
                .createdAt(powerStats.getCreatedAt() != null ? powerStats.getCreatedAt() : Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public PowerStats toDomain() {
        return PowerStats.builder()
                .id(id)
                .strength(strength)
                .agility(agility)
                .dexterity(dexterity)
                .intelligence(intelligence)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}