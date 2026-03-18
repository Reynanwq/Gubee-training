package br.com.gubee.interview.core.e2e;

import br.com.gubee.interview.core.Application;
import br.com.gubee.interview.core.features.hero.BaseIntegrationTest;
import br.com.gubee.interview.model.dto.request.CreateHeroRequest;
import br.com.gubee.interview.model.domain.enums.Race;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = Application.class
)
@AutoConfigureMockMvc
public class HeroE2EIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    }

    @Test
    void shouldCompleteFullHeroLifecycle() throws Exception {
        CreateHeroRequest createRequest = CreateHeroRequest.builder()
                .name("Superman")
                .race(Race.ALIEN)
                .strength(10)
                .agility(8)
                .dexterity(9)
                .intelligence(7)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/heroes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Herói cadastrado com sucesso!"))
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        UUID heroId = UUID.fromString(
                objectMapper.readTree(createResponse).get("id").asText()
        );

        mockMvc.perform(get("/api/v1/heroes/" + heroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(heroId.toString()))
                .andExpect(jsonPath("$.data.name").value("Superman"))
                .andExpect(jsonPath("$.data.race").value("ALIEN"))
                .andExpect(jsonPath("$.data.strength").value(10))
                .andExpect(jsonPath("$.data.agility").value(8))
                .andExpect(jsonPath("$.data.dexterity").value(9))
                .andExpect(jsonPath("$.data.intelligence").value(7));

        CreateHeroRequest updateRequest = CreateHeroRequest.builder()
                .name("Superman - The Man of Steel")
                .race(Race.ALIEN)
                .strength(10)
                .agility(8)
                .dexterity(9)
                .intelligence(7)
                .build();

        mockMvc.perform(put("/api/v1/heroes/" + heroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/heroes/" + heroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(heroId.toString()))
                .andExpect(jsonPath("$.data.name").value("Superman - The Man of Steel"))
                .andExpect(jsonPath("$.data.race").value("ALIEN"));

        mockMvc.perform(delete("/api/v1/heroes/" + heroId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/heroes/" + heroId))
                .andExpect(status().isNotFound());
    }
}