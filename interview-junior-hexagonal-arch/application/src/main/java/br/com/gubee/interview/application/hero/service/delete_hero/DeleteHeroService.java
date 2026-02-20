package br.com.gubee.interview.application.hero.service.delete_hero;

import br.com.gubee.interview.application.hero.useCases.DeleteHeroUseCase;
import br.com.gubee.interview.domain.adapters.DeleteHeroPort;

public class DeleteHeroService implements DeleteHeroUseCase {

    private DeleteHeroPort heroRepository;

    public DeleteHeroService(DeleteHeroPort heroRepository) {
        this.heroRepository = heroRepository;
    }

    @Override
    public void execute(String id) {
        heroRepository.deleteById(id);
    }
}
