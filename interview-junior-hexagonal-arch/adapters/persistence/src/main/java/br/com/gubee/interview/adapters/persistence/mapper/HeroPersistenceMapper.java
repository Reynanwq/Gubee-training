package br.com.gubee.interview.core.infrastructure.persistence.mappers;

import br.com.gubee.interview.core.infrastructure.persistence.entities.HeroEntity;
import br.com.gubee.interview.model.domain.entities.Hero;
import org.springframework.stereotype.Component;

@Component
public class HeroEntityMapper {

    public HeroEntity toEntity(Hero hero) {
        if (hero == null) return null;
        return HeroEntity.fromDomain(hero);
    }

    public Hero toDomain(HeroEntity entity) {
        if (entity == null) return null;
        return entity.toDomain();
    }
}