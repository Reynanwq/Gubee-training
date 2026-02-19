package br.com.gubee.interview.core.infrastructure.persistence.repositories;

import br.com.gubee.interview.core.infrastructure.persistence.entities.HeroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HeroJpaRepository extends JpaRepository<HeroEntity, UUID> {
    List<HeroEntity> findByNameContainingIgnoreCase(String name);
}