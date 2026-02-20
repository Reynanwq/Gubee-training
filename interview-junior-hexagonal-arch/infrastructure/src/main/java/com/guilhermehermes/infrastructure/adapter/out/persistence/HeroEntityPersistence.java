package com.guilhermehermes.infrastructure.adapter.out.persistence;

import br.com.gubee.interview.domain.enums.Race;
import br.com.gubee.interview.domain.model.Hero;
import br.com.gubee.interview.domain.vo.PowerStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "heroes")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class HeroEntityPersistence {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String name;

    private PowerStats powerStats;

    private Race race;

    Boolean enabled;

    public HeroEntityPersistence(Hero hero) {
        this.id = hero.getId() != null ? new ObjectId(hero.getId()) : null;
        this.name = hero.getName();
        this.powerStats = hero.getPowerStats();
        this.race = hero.getRace();
        this.enabled = hero.isEnabled();
    }

    public Hero toDomain() {
        return new Hero(this.id, this.name, this.race, this.powerStats, this.enabled);
    }
}
