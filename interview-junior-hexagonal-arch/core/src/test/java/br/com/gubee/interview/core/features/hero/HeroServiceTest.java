package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.application.usecases.hero.CreateHeroUseCase;
import br.com.gubee.interview.core.application.usecases.hero.DeleteHeroUseCase;
import br.com.gubee.interview.core.application.usecases.hero.FindHeroUseCase;
import br.com.gubee.interview.core.application.usecases.hero.UpdateHeroUseCase;
import br.com.gubee.interview.model.domain.entities.Hero;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import br.com.gubee.interview.model.domain.enums.Race;
import br.com.gubee.interview.model.domain.repositories.HeroRepository;
import br.com.gubee.interview.model.domain.repositories.PowerStatsRepository;
import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.dto.request.CreateHeroRequest;
import br.com.gubee.interview.model.dto.request.UpdateHeroRequest;
import br.com.gubee.interview.model.exceptions.HeroNotFoundException;
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
    private CreateHeroUseCase createHeroUseCase;

    @InjectMocks
    private FindHeroUseCase findHeroUseCase;

    @InjectMocks
    private UpdateHeroUseCase updateHeroUseCase;

    @InjectMocks
    private DeleteHeroUseCase deleteHeroUseCase;

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

        UUID result = createHeroUseCase.execute(createHeroRequest);

        assertThat(result).isEqualTo(heroId);
        verify(powerStatsRepository).create(any(PowerStats.class));
        verify(heroRepository).create(any(Hero.class));
    }

    @Test
    void findById_shouldReturnHeroDTOWhenHeroExists() {
        when(heroRepository.findById(heroId)).thenReturn(Optional.of(hero));
        when(powerStatsRepository.findById(powerStatsId)).thenReturn(Optional.of(powerStats));

        Optional<HeroDTO> result = findHeroUseCase.findById(heroId);

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

        Optional<HeroDTO> result = findHeroUseCase.findById(heroId);

        assertThat(result).isEmpty();
        verify(powerStatsRepository, never()).findById(any());
    }

    @Test
    void findById_shouldThrowExceptionWhenPowerStatsNotFound() {
        when(heroRepository.findById(heroId)).thenReturn(Optional.of(hero));
        when(powerStatsRepository.findById(powerStatsId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findHeroUseCase.findById(heroId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("PowerStats not found for hero: " + heroId);
    }

    @Test
    void update_shouldUpdateHeroNameSuccessfully() {
        UpdateHeroRequest updateRequest = UpdateHeroRequest.builder()
                .name("Superman Updated")
                .build();

        when(heroRepository.findById(heroId)).thenReturn(Optional.of(hero));

        updateHeroUseCase.execute(heroId, updateRequest);

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

        updateHeroUseCase.execute(heroId, updateRequest);

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

        assertThatThrownBy(() -> updateHeroUseCase.execute(heroId, updateRequest))
                .isInstanceOf(HeroNotFoundException.class)
                .hasMessageContaining(heroId.toString());
    }

    @Test
    void delete_shouldDeleteHeroAndPowerStatsSuccessfully() {
        when(heroRepository.findById(heroId)).thenReturn(Optional.of(hero));

        deleteHeroUseCase.execute(heroId);

        verify(heroRepository).delete(heroId);
        verify(powerStatsRepository).delete(powerStatsId);
    }

    @Test
    void delete_shouldThrowHeroNotFoundExceptionWhenHeroNotFound() {
        when(heroRepository.findById(heroId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteHeroUseCase.execute(heroId))
                .isInstanceOf(HeroNotFoundException.class)
                .hasMessageContaining(heroId.toString());
    }
}