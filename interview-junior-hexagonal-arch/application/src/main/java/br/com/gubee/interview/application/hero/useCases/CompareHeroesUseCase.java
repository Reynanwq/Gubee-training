package br.com.gubee.interview.application.hero.useCases;

import br.com.gubee.interview.application.hero.service.compare_heroes.CompareHeroesInput;

import java.util.Map;

public interface CompareHeroesUseCase {

    Map<String, Object> execute(CompareHeroesInput input);
}
