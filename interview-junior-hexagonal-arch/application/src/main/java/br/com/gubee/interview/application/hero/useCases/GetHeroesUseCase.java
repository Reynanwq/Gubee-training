package br.com.gubee.interview.application.hero.useCases;

import br.com.gubee.interview.domain.model.Hero;

import java.util.List;

public interface GetHeroesUseCase {
    public List<Hero> execute();
}
