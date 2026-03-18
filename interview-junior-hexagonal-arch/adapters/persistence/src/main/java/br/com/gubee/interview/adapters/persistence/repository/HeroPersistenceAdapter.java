package br.com.gubee.interview.core.infrastructure.persistence.repositories;

import br.com.gubee.interview.core.infrastructure.persistence.entities.HeroEntity;
import br.com.gubee.interview.core.infrastructure.persistence.mappers.HeroEntityMapper;
import br.com.gubee.interview.model.domain.entities.Hero;
import br.com.gubee.interview.model.domain.repositories.HeroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HeroRepositoryImpl implements HeroRepository {

    private final HeroJpaRepository jpaRepository;
    private final HeroEntityMapper mapper;

    @Override
    public UUID create(Hero hero) {
        HeroEntity entity = mapper.toEntity(hero);
        HeroEntity saved = jpaRepository.save(entity);
        return saved.getId();
    }

    @Override
    public Optional<Hero> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Hero> findByName(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Hero hero) {
        jpaRepository.findById(hero.getId())
                .map(entity -> {
                    entity.setName(hero.getName());
                    entity.setRace(hero.getRace());
                    return jpaRepository.save(entity);
                });
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}