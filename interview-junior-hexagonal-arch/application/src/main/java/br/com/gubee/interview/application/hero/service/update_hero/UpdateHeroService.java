package br.com.gubee.interview.application.hero.service.update_hero;

import br.com.gubee.interview.application.hero.useCases.UpdateHeroUseCase;
import br.com.gubee.interview.application.hero.exception.ObjectNotFoundException;
import br.com.gubee.interview.domain.adapters.LoadHeroPort;
import br.com.gubee.interview.domain.adapters.SaveHeroPort;
import br.com.gubee.interview.domain.model.Hero;

import java.util.Optional;

public class UpdateHeroService implements UpdateHeroUseCase {

    private final SaveHeroPort heroRepository;
    private final LoadHeroPort loadHeroPort;

    public UpdateHeroService(SaveHeroPort heroRepository, LoadHeroPort loadHeroPort) {
        this.heroRepository = heroRepository;
        this.loadHeroPort = loadHeroPort;
    }



    public Hero execute(UpdateHeroInput input) {
        return loadHeroPort.findById(input.getId())
                .map(hero -> {
                    Optional.ofNullable(input.getName()).ifPresent(hero::setName);
                    Optional.ofNullable(input.getRace()).ifPresent(hero::setRace);
                    Optional.ofNullable(input.getPowerStats()).ifPresent(hero::setPowerStats);
                    Optional.ofNullable(input.getEnabled()).ifPresent(hero::setEnabled);
                    return heroRepository.save(hero);
                })
                .orElseThrow(() -> new ObjectNotFoundException(input.getId()));
    }

}
