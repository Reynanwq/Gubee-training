package br.com.gubee.interview.model.dto;

import br.com.gubee.interview.model.domain.enums.Race;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeroDTO {
    private UUID id;
    private String name;
    private Race race;
    private UUID powerStatsId;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean enabled;
    private Integer strength;
    private Integer agility;
    private Integer dexterity;
    private Integer intelligence;
}