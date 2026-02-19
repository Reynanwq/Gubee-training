package br.com.gubee.interview.core.infrastructure.persistence.repositories;

import br.com.gubee.interview.core.infrastructure.persistence.entities.PowerStatsEntity;
import br.com.gubee.interview.core.infrastructure.persistence.mappers.PowerStatsEntityMapper;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import br.com.gubee.interview.model.domain.repositories.PowerStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PowerStatsRepositoryImpl implements PowerStatsRepository {

    private final PowerStatsJpaRepository jpaRepository;
    private final PowerStatsEntityMapper mapper;

    @Override
    public UUID create(PowerStats powerStats) {
        PowerStatsEntity entity = mapper.toEntity(powerStats);
        PowerStatsEntity saved = jpaRepository.save(entity);
        return saved.getId();
    }

    @Override
    public Optional<PowerStats> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void update(PowerStats powerStats) {
        jpaRepository.findById(powerStats.getId())
                .map(entity -> {
                    entity.setStrength((short) powerStats.getStrength());  // CAST EXPLÍCITO
                    entity.setAgility((short) powerStats.getAgility());    // CAST EXPLÍCITO
                    entity.setDexterity((short) powerStats.getDexterity()); // CAST EXPLÍCITO
                    entity.setIntelligence((short) powerStats.getIntelligence()); // CAST EXPLÍCITO
                    return jpaRepository.save(entity);
                });
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}