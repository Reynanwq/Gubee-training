package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.enums.Race;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class HeroRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String CREATE_HERO_QUERY = "INSERT INTO hero" +
            " (name, race, power_stats_id)" +
            " VALUES (:name, :race, :powerStatsId) RETURNING id";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM hero WHERE id = :id";

    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM hero WHERE name ILIKE :name";

    private static final String UPDATE_HERO_QUERY = "UPDATE hero SET name = :name, race = :race, updated_at = :updatedAt WHERE id = :id";

    private static final String DELETE_HERO_QUERY = "DELETE FROM hero WHERE id = :id";

    public UUID create(Hero hero) {
        final Map<String, Object> params = Map.of(
                "name", hero.getName(),
                "race", hero.getRace().name(),
                "powerStatsId", hero.getPowerStatsId()
        );
        return namedParameterJdbcTemplate.queryForObject(CREATE_HERO_QUERY, params, UUID.class);
    }

    public Optional<Hero> findById(UUID id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        List<Hero> heroes = namedParameterJdbcTemplate.query(FIND_BY_ID_QUERY, params, heroRowMapper());
        return heroes.stream().findFirst();
    }

    public List<Hero> findByName(String name) {
        Map<String, Object> params = Collections.singletonMap("name", "%" + name + "%");
        return namedParameterJdbcTemplate.query(FIND_BY_NAME_QUERY, params, heroRowMapper());
    }

    public void update(Hero hero) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", hero.getId());
        params.put("name", hero.getName());
        params.put("race", hero.getRace().name());
        params.put("updatedAt", Timestamp.from(Instant.now()));

        namedParameterJdbcTemplate.update(UPDATE_HERO_QUERY, params);
    }

    public void delete(UUID id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        namedParameterJdbcTemplate.update(DELETE_HERO_QUERY, params);
    }

    private RowMapper<Hero> heroRowMapper() {
        return (rs, rowNum) -> Hero.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .race(Race.valueOf(rs.getString("race")))
                .powerStatsId(UUID.fromString(rs.getString("power_stats_id")))
                .createdAt(rs.getTimestamp("created_at").toInstant())
                .updatedAt(rs.getTimestamp("updated_at") != null ?
                        rs.getTimestamp("updated_at").toInstant() : null)
                .enabled(rs.getBoolean("enabled"))
                .build();
    }
}
