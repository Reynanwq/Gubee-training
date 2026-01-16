package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.exception.HeroNotFoundException;
import br.com.gubee.interview.core.features.powerstats.PowerStatsRepository;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.enums.Race;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<Hero> findById(UUID id) {
        return heroRepository.findById(id);
    }

    public List<Hero> findByName(String name) {
        return heroRepository.findByName(name);
    }

    @Transactional
    public void update(UUID id, Map<String, Object> updates) {
        Hero hero = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));

        PowerStats powerStats = powerStatsRepository.findById(hero.getPowerStatsId())
                .orElseThrow(() -> new RuntimeException("PowerStats not found for hero: " + id));

        if (updates.containsKey("name")) {
            hero.setName((String) updates.get("name"));
        }
        if (updates.containsKey("race")) {
            hero.setRace(Race.valueOf((String) updates.get("race")));
        }
        heroRepository.update(hero);

        if (updates.containsKey("strength")) {
            powerStats.setStrength((Integer) updates.get("strength"));
        }
        if (updates.containsKey("agility")) {
            powerStats.setAgility((Integer) updates.get("agility"));
        }
        if (updates.containsKey("dexterity")) {
            powerStats.setDexterity((Integer) updates.get("dexterity"));
        }
        if (updates.containsKey("intelligence")) {
            powerStats.setIntelligence((Integer) updates.get("intelligence"));
        }
        powerStatsRepository.update(powerStats);
    }

    @Transactional
    public void delete(UUID id) {
        Hero hero = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));

        heroRepository.delete(id);

        powerStatsRepository.delete(hero.getPowerStatsId());
    }
}
