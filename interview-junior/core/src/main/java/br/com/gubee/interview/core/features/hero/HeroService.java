package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.exception.HeroNotFoundException;
import br.com.gubee.interview.core.features.powerstats.PowerStatsRepository;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HeroService {

    private final HeroRepository heroRepository;
    private final PowerStatsRepository powerStatsRepository;

    @Transactional
    public UUID create(CreateHeroRequest createHeroRequest) {
        PowerStats powerStats = new PowerStats(createHeroRequest);
        UUID powerStatsId = powerStatsRepository.create(powerStats);
        return heroRepository.create(new Hero(createHeroRequest, powerStatsId));
    }

    public Optional<HeroDTO> findById(UUID id) {
        Optional<Hero> heroOptional = heroRepository.findById(id);

        if (heroOptional.isEmpty()) {
            return Optional.empty();
        }

        Hero hero = heroOptional.get();
        Optional<PowerStats> powerStatsOptional = powerStatsRepository.findById(hero.getPowerStatsId());

        if (powerStatsOptional.isEmpty()) {
            throw new RuntimeException("PowerStats not found for hero: " + id);
        }

        PowerStats powerStats = powerStatsOptional.get();
        return Optional.of(HeroDTO.fromHeroAndPowerStats(hero, powerStats));
    }

    public List<HeroDTO> findByName(String name) {
        List<Hero> heroes = heroRepository.findByName(name);

        return heroes.stream()
                .map(hero -> {
                    Optional<PowerStats> powerStatsOptional = powerStatsRepository.findById(hero.getPowerStatsId());
                    if (powerStatsOptional.isEmpty()) {
                        throw new RuntimeException("PowerStats not found for hero: " + hero.getId());
                    }
                    return HeroDTO.fromHeroAndPowerStats(hero, powerStatsOptional.get());
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(UUID id, UpdateHeroRequest updates) {
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
    @Transactional
    public void delete(UUID id) {
        Hero hero = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));

        heroRepository.delete(id);
        powerStatsRepository.delete(hero.getPowerStatsId());
    }
}