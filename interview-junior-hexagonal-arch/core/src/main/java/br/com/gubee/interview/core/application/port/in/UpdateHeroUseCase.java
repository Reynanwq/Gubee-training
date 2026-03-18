package br.com.gubee.interview.core.application.usecases.hero;

import br.com.gubee.interview.model.domain.entities.Hero;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import br.com.gubee.interview.model.domain.repositories.HeroRepository;
import br.com.gubee.interview.model.domain.repositories.PowerStatsRepository;
import br.com.gubee.interview.model.dto.request.UpdateHeroRequest;
import br.com.gubee.interview.model.exceptions.HeroNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateHeroUseCase {

    private final HeroRepository heroRepository;
    private final PowerStatsRepository powerStatsRepository;

    @Transactional
    public void execute(UUID id, UpdateHeroRequest updates) {
        Hero hero = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));

        boolean heroUpdated = false;
        if (updates.getName() != null && !updates.getName().equals(hero.getName())) {
            hero.setName(updates.getName());
            heroUpdated = true;
        }
        if (updates.getRace() != null && updates.getRace() != hero.getRace()) {
            hero.setRace(updates.getRace());
            heroUpdated = true;
        }

        if (heroUpdated) {
            heroRepository.update(hero);
        }

        if (updates.getStrength() != null || updates.getAgility() != null ||
                updates.getDexterity() != null || updates.getIntelligence() != null) {

            PowerStats powerStats = powerStatsRepository.findById(hero.getPowerStatsId())
                    .orElseThrow(() -> new RuntimeException("PowerStats not found for hero: " + id));

            boolean powerStatsUpdated = false;
            if (updates.getStrength() != null && !updates.getStrength().equals(powerStats.getStrength())) {
                powerStats.setStrength(updates.getStrength());
                powerStatsUpdated = true;
            }
            if (updates.getAgility() != null && !updates.getAgility().equals(powerStats.getAgility())) {
                powerStats.setAgility(updates.getAgility());
                powerStatsUpdated = true;
            }
            if (updates.getDexterity() != null && !updates.getDexterity().equals(powerStats.getDexterity())) {
                powerStats.setDexterity(updates.getDexterity());
                powerStatsUpdated = true;
            }
            if (updates.getIntelligence() != null && !updates.getIntelligence().equals(powerStats.getIntelligence())) {
                powerStats.setIntelligence(updates.getIntelligence());
                powerStatsUpdated = true;
            }

            if (powerStatsUpdated) {
                powerStatsRepository.update(powerStats);
            }
        }
    }
}