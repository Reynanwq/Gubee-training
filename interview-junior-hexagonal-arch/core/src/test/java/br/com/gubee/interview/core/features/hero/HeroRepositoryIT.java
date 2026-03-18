package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.infrastructure.persistence.entities.HeroEntity;
import br.com.gubee.interview.core.infrastructure.persistence.entities.PowerStatsEntity;
import br.com.gubee.interview.model.domain.entities.Hero;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import br.com.gubee.interview.model.domain.enums.Race;
import br.com.gubee.interview.model.domain.repositories.HeroRepository;
import br.com.gubee.interview.model.domain.repositories.PowerStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("it")
@Transactional
class HeroRepositoryIT {

    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private PowerStatsRepository powerStatsRepository;

    private UUID powerStatsId;
    private Hero hero;

    @BeforeEach
    void setUp() {
        PowerStats powerStats = PowerStats.builder()
                .strength(10)
                .agility(8)
                .dexterity(9)
                .intelligence(7)
                .build();

        powerStatsId = powerStatsRepository.create(powerStats);

        hero = Hero.builder()
                .name("Superman")
                .race(Race.ALIEN)
                .powerStatsId(powerStatsId)
                .enabled(true)
                .build();
    }

    @Test
    void create_shouldPersistHeroInDatabase() {
        UUID heroId = heroRepository.create(hero);

        Optional<Hero> savedHero = heroRepository.findById(heroId);
        assertThat(savedHero).isPresent();
        assertThat(savedHero.get().getName()).isEqualTo("Superman");
        assertThat(savedHero.get().getRace()).isEqualTo(Race.ALIEN);
        assertThat(savedHero.get().getPowerStatsId()).isEqualTo(powerStatsId);
    }

    @Test
    void findById_shouldReturnHeroWhenExists() {
        UUID heroId = heroRepository.create(hero);

        Optional<Hero> result = heroRepository.findById(heroId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(heroId);
    }

    @Test
    void findById_shouldReturnEmptyWhenHeroNotFound() {
        Optional<Hero> result = heroRepository.findById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void findByName_shouldReturnHeroWhenNameMatches() {
        heroRepository.create(hero);

        List<Hero> results = heroRepository.findByName("super");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Superman");
    }

    @Test
    void update_shouldUpdateHeroInDatabase() {
        UUID heroId = heroRepository.create(hero);

        Optional<Hero> heroOptional = heroRepository.findById(heroId);
        assertThat(heroOptional).isPresent();

        Hero heroToUpdate = heroOptional.get();
        heroToUpdate.setName("Superman Updated");
        heroToUpdate.setRace(Race.DIVINE);

        heroRepository.update(heroToUpdate);

        Optional<Hero> updatedHero = heroRepository.findById(heroId);
        assertThat(updatedHero).isPresent();
        assertThat(updatedHero.get().getName()).isEqualTo("Superman Updated");
        assertThat(updatedHero.get().getRace()).isEqualTo(Race.DIVINE);
    }

    @Test
    void delete_shouldRemoveHeroFromDatabase() {
        UUID heroId = heroRepository.create(hero);
        assertThat(heroRepository.findById(heroId)).isPresent();

        heroRepository.delete(heroId);

        assertThat(heroRepository.findById(heroId)).isEmpty();
    }
}