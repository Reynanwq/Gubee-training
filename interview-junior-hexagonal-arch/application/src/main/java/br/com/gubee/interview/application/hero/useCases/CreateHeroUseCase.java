package br.com.gubee.interview.application.hero.useCases;

import br.com.gubee.interview.application.hero.service.create_hero.CreateHeroInput;

import java.util.Map;

public interface CreateHeroUseCase {

    public Map<String, Object> execute(CreateHeroInput input);

}
