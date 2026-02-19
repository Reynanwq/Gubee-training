package br.com.gubee.interview.model.domain.repositories;

import br.com.gubee.interview.model.domain.entities.PowerStats;

import java.util.Optional;
import java.util.UUID;

public interface PowerStatsRepository {
    UUID create(PowerStats powerStats);
    Optional<PowerStats> findById(UUID id);
    void update(PowerStats powerStats);
    void delete(UUID id);
}