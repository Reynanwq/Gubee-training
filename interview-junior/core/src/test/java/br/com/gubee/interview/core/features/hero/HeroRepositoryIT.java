package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.core.features.powerstats.PowerStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class HeroRepositoryIT extends BaseIntegrationTest{
    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private PowerStatsRepository powerStatsRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID powerStatsId;
    private Hero hero;

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS public");

        jdbcTemplate.execute("SET search_path TO public");

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS power_stats (" +
                        "    id           UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid()," +
                        "    strength     SMALLINT         NOT NULL," +
                        "    agility      SMALLINT         NOT NULL," +
                        "    dexterity    SMALLINT         NOT NULL," +
                        "    intelligence SMALLINT         NOT NULL," +
                        "    created_at   TIMESTAMPTZ      NOT NULL DEFAULT now()," +
                        "    updated_at   TIMESTAMPTZ      NOT NULL DEFAULT now()" +
                        ")"
        );

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS hero (" +
                        "    id             UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid()," +
                        "    name           VARCHAR(255)     NOT NULL UNIQUE," +
                        "    race           VARCHAR(255)     NOT NULL," +
                        "    power_stats_id UUID             NOT NULL," +
                        "    enabled        BOOLEAN          NOT NULL DEFAULT TRUE," +
                        "    created_at     TIMESTAMPTZ      NOT NULL DEFAULT now()," +
                        "    updated_at     TIMESTAMPTZ      NOT NULL DEFAULT now()," +
                        "    CHECK ( race IN ('HUMAN', 'ALIEN', 'DIVINE', 'CYBORG'))," +
                        "    CONSTRAINT FK_power_stats FOREIGN KEY (power_stats_id) REFERENCES power_stats(id)" +
                        ")"
        );

        jdbcTemplate.execute("DELETE FROM hero");
        jdbcTemplate.execute("DELETE FROM power_stats");

        PowerStats powerStats = PowerStats.builder()
                .strength(10)
                .agility(8)
                .dexterity(9)
                .intelligence(7)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        powerStatsId = powerStatsRepository.create(powerStats);

        hero = Hero.builder()
                .name("Superman")
                .race(Race.ALIEN)
                .powerStatsId(powerStatsId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
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

        Hero heroToUpdate = heroRepository.findById(heroId).get();
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
