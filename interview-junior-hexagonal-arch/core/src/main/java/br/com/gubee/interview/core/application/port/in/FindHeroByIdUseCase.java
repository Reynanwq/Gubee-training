package br.com.gubee.interview.core.application.usecases.hero;

import br.com.gubee.interview.model.domain.entities.Hero;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import br.com.gubee.interview.model.domain.repositories.HeroRepository;
import br.com.gubee.interview.model.domain.repositories.PowerStatsRepository;
import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.exceptions.HeroNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FindHeroUseCase {

    private final HeroRepository heroRepository;
    private final PowerStatsRepository powerStatsRepository;

    public Optional<HeroDTO> findById(UUID id) {
        Optional<Hero> heroOptional = heroRepository.findById(id);

        if (heroOptional.isEmpty()) {
            return Optional.empty();
        }

        Hero hero = heroOptional.get();
        PowerStats powerStats = powerStatsRepository.findById(hero.getPowerStatsId())
                .orElseThrow(() -> new RuntimeException("PowerStats not found for hero: " + id));

        return Optional.of(mapToDTO(hero, powerStats));
    }

    public List<HeroDTO> findByName(String name) {
        List<Hero> heroes = heroRepository.findByName(name);

        return heroes.stream()
                .map(hero -> {
                    PowerStats powerStats = powerStatsRepository.findById(hero.getPowerStatsId())
                            .orElseThrow(() -> new RuntimeException("PowerStats not found for hero: " + hero.getId()));
                    return mapToDTO(hero, powerStats);
                })
                .collect(Collectors.toList());
    }

    private HeroDTO mapToDTO(Hero hero, PowerStats powerStats) {
        return HeroDTO.builder()
                .id(hero.getId())
                .name(hero.getName())
                .race(hero.getRace())
                .powerStatsId(hero.getPowerStatsId())
                .createdAt(hero.getCreatedAt())
                .updatedAt(hero.getUpdatedAt())
                .enabled(hero.isEnabled())
                .strength(powerStats.getStrength())
                .agility(powerStats.getAgility())
                .dexterity(powerStats.getDexterity())
                .intelligence(powerStats.getIntelligence())
                .build();
    }
}