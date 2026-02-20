package br.com.gubee.interview.domain.adapters;

import br.com.gubee.interview.domain.model.Hero;

import java.util.List;
import java.util.Optional;

public interface LoadHeroPort {

    Optional<Hero> findById(String id);

    List<Hero> findByName(String name);

    List<Hero> findAll();

}
