package br.com.gubee.interview.core.application.usecases.hero;

import br.com.gubee.interview.model.domain.entities.Hero;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import br.com.gubee.interview.model.domain.repositories.HeroRepository;
import br.com.gubee.interview.model.domain.repositories.PowerStatsRepository;
import br.com.gubee.interview.model.dto.request.CreateHeroRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateHeroUseCase {

    private final HeroRepository heroRepository;
    private final PowerStatsRepository powerStatsRepository;

    @Transactional
    public UUID execute(CreateHeroRequest request) {
        PowerStats powerStats = PowerStats.builder()
                .strength(request.getStrength())
                .agility(request.getAgility())
                .dexterity(request.getDexterity())
                .intelligence(request.getIntelligence())
                .build();

        UUID powerStatsId = powerStatsRepository.create(powerStats);

        Hero hero = Hero.builder()
                .name(request.getName())
                .race(request.getRace())
                .powerStatsId(powerStatsId)
                .enabled(true)
                .build();

        return heroRepository.create(hero);
    }
}
