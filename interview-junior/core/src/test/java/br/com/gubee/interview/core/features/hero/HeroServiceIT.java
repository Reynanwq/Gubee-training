package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("it")
public class HeroServiceIT {

    @MockBean
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private HeroService heroService;

    @MockBean
    private DataSource dataSource; // Adicione isso

    @MockBean
    private JdbcTemplate jdbcTemplate; // Adicione isso também se necessário

    @Test
    public void createHeroWithAllRequiredArguments() {
        // Arrange
        UUID mockHeroId = UUID.randomUUID();
        when(namedParameterJdbcTemplate.queryForObject(
                anyString(),
                anyMap(),
                any(Class.class)))
                .thenReturn(mockHeroId);

        // Act
        UUID heroId = heroService.create(createHeroRequest());

        // Assert
        assertNotNull(heroId);
    }

    private CreateHeroRequest createHeroRequest() {
        return CreateHeroRequest.builder()
                .name("Batman")
                .agility(5)
                .dexterity(8)
                .strength(6)
                .intelligence(10)
                .race(Race.HUMAN)
                .build();
    }
}