package br.com.gubee.interview.core.features.powerstats;

import br.com.gubee.interview.model.PowerStats;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class PowerStatsRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String CREATE_POWER_STATS_QUERY = "INSERT INTO power_stats" +
            " (strength, agility, dexterity, intelligence)" +
            " VALUES (:strength, :agility, :dexterity, :intelligence) RETURNING id";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM power_stats WHERE id = :id";

    private static final String UPDATE_POWER_STATS_QUERY = "UPDATE power_stats SET " +
            "strength = :strength, agility = :agility, dexterity = :dexterity, " +
            "intelligence = :intelligence, updated_at = :updatedAt WHERE id = :id";

    private static final String DELETE_POWER_STATS_QUERY = "DELETE FROM power_stats WHERE id = :id";

    public UUID create(PowerStats powerStats) {
        return namedParameterJdbcTemplate.queryForObject(
                CREATE_POWER_STATS_QUERY,
                new BeanPropertySqlParameterSource(powerStats),
                UUID.class);
    }

    public Optional<PowerStats> findById(UUID id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        List<PowerStats> powerStatsList = namedParameterJdbcTemplate.query(
                FIND_BY_ID_QUERY, params, powerStatsRowMapper());
        return powerStatsList.stream().findFirst();
    }

    public void update(PowerStats powerStats) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", powerStats.getId());
        params.put("strength", powerStats.getStrength());
        params.put("agility", powerStats.getAgility());
        params.put("dexterity", powerStats.getDexterity());
        params.put("intelligence", powerStats.getIntelligence());
        params.put("updatedAt", Timestamp.from(Instant.now()));

        namedParameterJdbcTemplate.update(UPDATE_POWER_STATS_QUERY, params);
    }

    public void delete(UUID id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        namedParameterJdbcTemplate.update(DELETE_POWER_STATS_QUERY, params);
    }

    private RowMapper<PowerStats> powerStatsRowMapper() {
        return (rs, rowNum) -> PowerStats.builder()
                .id(UUID.fromString(rs.getString("id")))
                .strength(rs.getInt("strength"))
                .agility(rs.getInt("agility"))
                .dexterity(rs.getInt("dexterity"))
                .intelligence(rs.getInt("intelligence"))
                .createdAt(rs.getTimestamp("created_at").toInstant())
                .updatedAt(rs.getTimestamp("updated_at") != null ?
                        rs.getTimestamp("updated_at").toInstant() : null)
                .build();
    }
}
