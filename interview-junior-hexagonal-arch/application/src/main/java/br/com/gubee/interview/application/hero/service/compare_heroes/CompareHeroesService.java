package br.com.gubee.interview.application.hero.service.compare_heroes;

import br.com.gubee.interview.application.hero.useCases.CompareHeroesUseCase;
import br.com.gubee.interview.application.hero.exception.ObjectNotFoundException;
import br.com.gubee.interview.domain.adapters.LoadHeroPort;
import br.com.gubee.interview.domain.model.Hero;
import br.com.gubee.interview.domain.vo.PowerStats;

import java.util.HashMap;
import java.util.Map;

public class CompareHeroesService implements CompareHeroesUseCase {


    private final LoadHeroPort heroRepository;

    public CompareHeroesService(LoadHeroPort heroRepository) {
        this.heroRepository = heroRepository;
    }

    @Override
    public Map<String, Object> execute(CompareHeroesInput input) {
        Hero hero1 = heroRepository.findById(input.getId1()).orElseThrow(() -> new ObjectNotFoundException(input.getId1()));
        Hero hero2 = heroRepository.findById(input.getId2()).orElseThrow(() -> new ObjectNotFoundException(input.getId1()));

        Map<String, Object> result = new HashMap<>();

        result.put("id1", hero1.getId().toString());
        result.put("id2", hero2.getId().toString());

        PowerStats powerStats1 = hero1.getPowerStats();
        PowerStats powerStats2 = hero2.getPowerStats();

        int strengthDiff = powerStats1.getStrength() - powerStats2.getStrength();
        int agilityDiff = powerStats1.getAgility() - powerStats2.getAgility();
        int dexterityDiff = powerStats1.getDexterity() - powerStats2.getDexterity();
        int intelligenceDiff = powerStats1.getIntelligence() - powerStats2.getIntelligence();

        result.put("strengthDiff", strengthDiff);
        result.put("agilityDiff", agilityDiff);
        result.put("dexterityDiff", dexterityDiff);
        result.put("intelligenceDiff", intelligenceDiff);

        return result;

    }

}


