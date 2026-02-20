package br.com.gubee.interview.application.hero.service;

import br.com.gubee.interview.application.hero.useCases.GetHeroesUseCase;
import br.com.gubee.interview.domain.adapters.LoadHeroPort;
import br.com.gubee.interview.domain.model.Hero;

import java.util.List;

public class GetHeroesService implements GetHeroesUseCase {

    private final LoadHeroPort heroRepository;

    public GetHeroesService(LoadHeroPort heroRepository) {
        this.heroRepository = heroRepository;
    }

    public List<Hero> execute(){
        return heroRepository.findAll();
    }
}
