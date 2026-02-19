package br.com.gubee.interview.core.infrastructure.persistence.repositories;

import br.com.gubee.interview.core.infrastructure.persistence.entities.PowerStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PowerStatsJpaRepository extends JpaRepository<PowerStatsEntity, UUID> {
}