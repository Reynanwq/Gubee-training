package br.com.gubee.interview.core.application.usecases.hero;

import br.com.gubee.interview.model.domain.entities.Hero;
import br.com.gubee.interview.model.domain.repositories.HeroRepository;
import br.com.gubee.interview.model.domain.repositories.PowerStatsRepository;
import br.com.gubee.interview.model.exceptions.HeroNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteHeroUseCase {

    private final HeroRepository heroRepository;
    private final PowerStatsRepository powerStatsRepository;

    @Transactional
    public void execute(UUID id) {
        Hero hero = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));

        heroRepository.delete(id);
        powerStatsRepository.delete(hero.getPowerStatsId());
    }
}