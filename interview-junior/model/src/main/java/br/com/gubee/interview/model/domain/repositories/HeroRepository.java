package br.com.gubee.interview.model.domain.repositories;

import br.com.gubee.interview.model.domain.entities.Hero;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HeroRepository {
    UUID create(Hero hero);
    Optional<Hero> findById(UUID id);
    List<Hero> findByName(String name);
    void update(Hero hero);
    void delete(UUID id);
}