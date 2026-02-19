package br.com.gubee.interview.core.infrastructure.persistence.mappers;

import br.com.gubee.interview.core.infrastructure.persistence.entities.PowerStatsEntity;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import org.springframework.stereotype.Component;

@Component
public class PowerStatsEntityMapper {

    public PowerStatsEntity toEntity(PowerStats powerStats) {
        if (powerStats == null) return null;
        return PowerStatsEntity.fromDomain(powerStats);
    }

    public PowerStats toDomain(PowerStatsEntity entity) {
        if (entity == null) return null;
        return entity.toDomain();
    }
}