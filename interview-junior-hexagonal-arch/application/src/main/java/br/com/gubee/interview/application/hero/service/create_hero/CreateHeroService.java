package br.com.gubee.interview.application.hero.service.create_hero;

import br.com.gubee.interview.application.hero.useCases.CreateHeroUseCase;
import br.com.gubee.interview.domain.adapters.SaveHeroPort;
import br.com.gubee.interview.domain.model.Hero;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

 public class CreateHeroService implements CreateHeroUseCase {

    private final SaveHeroPort heroRepository;

    public CreateHeroService(SaveHeroPort heroRepository) {
        this.heroRepository = heroRepository;
    }

    public  Map<String, Object> execute(CreateHeroInput input) {
        Hero hero = heroRepository.save(input.toHero());
        Map<String, Object> response = new HashMap<>();
        response.put("id", hero.getId().toString());
        response.put("timestamp", Instant.now().getEpochSecond());
        response.put("date", new Date());
        return response;

    }
}
