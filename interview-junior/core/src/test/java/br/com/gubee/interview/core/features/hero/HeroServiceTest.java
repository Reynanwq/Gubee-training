package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.exception.HeroNotFoundException;
import br.com.gubee.interview.core.features.powerstats.PowerStatsRepository;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeroServiceTest {

    @Mock
    private HeroRepository heroRepository;

    @Mock
    private PowerStatsRepository powerStatsRepository;

    @InjectMocks
    private HeroService heroService;

    private UUID heroId;
    private UUID powerStatsId;
    private Hero hero;
    private PowerStats powerStats;
    private CreateHeroRequest createHeroRequest;

    @BeforeEach
    void setUp() {
        heroId = UUID.randomUUID();
        powerStatsId = UUID.randomUUID();

        hero = Hero.builder()
                .id(heroId)
                .name("Superman")
                .race(Race.ALIEN)
                .powerStatsId(powerStatsId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .enabled(true)
                .build();

        powerStats = PowerStats.builder()
                .id(powerStatsId)
                .strength(100)
                .agility(80)
                .dexterity(90)
                .intelligence(70)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        createHeroRequest = CreateHeroRequest.builder()
                .name("Superman")
                .race(Race.ALIEN)
                .strength(100)
                .agility(80)
                .dexterity(90)
                .intelligence(70)
                .build();
    }

    @Test
    void create_shouldCreateHeroAndPowerStatsSuccessfully() {
        when(powerStatsRepository.create(any(PowerStats.class))).thenReturn(powerStatsId);
        when(heroRepository.create(any(Hero.class))).thenReturn(heroId);

        UUID result = heroService.create(createHeroRequest);

        assertThat(result).isEqualTo(heroId);

        verify(powerStatsRepository).create(any(PowerStats.class));
        verify(heroRepository).create(any(Hero.class));
    }

    @Test
    void findById_shouldReturnHeroDTOWhenHeroExists() {
        when(heroRepository.findById(heroId)).thenReturn(Optional.of(hero));
        when(powerStatsRepository.findById(powerStatsId)).thenReturn(Optional.of(powerStats));

        Optional<HeroDTO> result = heroService.findById(heroId);

        assertThat(result).isPresent();
        HeroDTO heroDTO = result.get();
        assertThat(heroDTO.getId()).isEqualTo(heroId);
        assertThat(heroDTO.getName()).isEqualTo("Superman");
        assertThat(heroDTO.getRace()).isEqualTo(Race.ALIEN);
        assertThat(heroDTO.getStrength()).isEqualTo(100);
        assertThat(heroDTO.getAgility()).isEqualTo(80);
        assertThat(heroDTO.getDexterity()).isEqualTo(90);
        assertThat(heroDTO.getIntelligence()).isEqualTo(70);
    }

    @Test
    void findById_shouldReturnEmptyWhenHeroNotFound() {
        when(heroRepository.findById(heroId)).thenReturn(Optional.empty());

        Optional<HeroDTO> result = heroService.findById(heroId);

        assertThat(result).isEmpty();
        verify(powerStatsRepository, never()).findById(any());
    }

    @Test
    void findById_shouldThrowExceptionWhenPowerStatsNotFound() {
        when(heroRepository.findById(heroId)).thenReturn(Optional.of(hero));
        when(powerStatsRepository.findById(powerStatsId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> heroService.findById(heroId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("PowerStats not found for hero: " + heroId);
    }

    @Test
    void update_shouldUpdateHeroNameSuccessfully() {
        UpdateHeroRequest updateRequest = UpdateHeroRequest.builder()
                .name("Superman Updated")
                .build();

        when(heroRepository.findById(heroId)).thenReturn(Optional.of(hero));

        heroService.update(heroId, updateRequest);

        assertThat(hero.getName()).isEqualTo("Superman Updated");
        verify(heroRepository).update(hero);
        verify(powerStatsRepository, never()).update(any());
    }

    @Test
    void update_shouldUpdatePowerStatsSuccessfully() {
        UpdateHeroRequest updateRequest = UpdateHeroRequest.builder()
                .strength(95)
                .agility(85)
                .build();

        when(heroRepository.findById(heroId)).thenReturn(Optional.of(hero));
        when(powerStatsRepository.findById(powerStatsId)).thenReturn(Optional.of(powerStats));

        heroService.update(heroId, updateRequest);

        assertThat(powerStats.getStrength()).isEqualTo(95);
        assertThat(powerStats.getAgility()).isEqualTo(85);
        verify(powerStatsRepository).update(powerStats);
    }

    @Test
    void update_shouldThrowHeroNotFoundExceptionWhenHeroNotFound() {
        UpdateHeroRequest updateRequest = UpdateHeroRequest.builder()
                .name("Updated Name")
                .build();

        when(heroRepository.findById(heroId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> heroService.update(heroId, updateRequest))
                .isInstanceOf(HeroNotFoundException.class)
                .hasMessageContaining(heroId.toString());
    }

    @Test
    void delete_shouldDeleteHeroAndPowerStatsSuccessfully() {
        when(heroRepository.findById(heroId)).thenReturn(Optional.of(hero));

        heroService.delete(heroId);

        verify(heroRepository).delete(heroId);
        verify(powerStatsRepository).delete(powerStatsId);
    }

    @Test
    void delete_shouldThrowHeroNotFoundExceptionWhenHeroNotFound() {
        when(heroRepository.findById(heroId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> heroService.delete(heroId))
                .isInstanceOf(HeroNotFoundException.class)
                .hasMessageContaining(heroId.toString());
    }
}
