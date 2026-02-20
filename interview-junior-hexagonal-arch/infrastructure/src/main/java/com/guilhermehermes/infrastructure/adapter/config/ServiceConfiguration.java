package com.guilhermehermes.infrastructure.adapter.config;

import br.com.gubee.interview.application.hero.service.GetHeroesService;
import br.com.gubee.interview.application.hero.service.compare_heroes.CompareHeroesService;
import br.com.gubee.interview.application.hero.service.create_hero.CreateHeroService;
import br.com.gubee.interview.application.hero.service.delete_hero.DeleteHeroService;
import br.com.gubee.interview.application.hero.service.update_hero.UpdateHeroService;
import br.com.gubee.interview.application.hero.useCases.*;
import com.guilhermehermes.infrastructure.adapter.out.persistence.HeroPersistenceAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    private final HeroPersistenceAdapter heroRepository;

    public ServiceConfiguration(HeroPersistenceAdapter heroRepository) {
        this.heroRepository = heroRepository;
    }

    @Bean
    CreateHeroUseCase createHeroUseCase(){
        return new CreateHeroService(heroRepository);
    }

    @Bean
    GetHeroesUseCase getHeroesUseCase() {
        return new GetHeroesService(heroRepository);
    }

    @Bean
    DeleteHeroUseCase deleteHeroUseCase(){
        return new DeleteHeroService(heroRepository);
    }

    @Bean
    UpdateHeroUseCase updateHeroUseCase(){
        return new UpdateHeroService(heroRepository, heroRepository);
    }

    @Bean
    CompareHeroesUseCase compareHeroesUseCase(){
        return new CompareHeroesService(heroRepository);
    }
}
