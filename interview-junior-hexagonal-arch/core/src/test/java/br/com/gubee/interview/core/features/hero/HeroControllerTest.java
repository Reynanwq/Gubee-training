package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.application.usecases.hero.CreateHeroUseCase;
import br.com.gubee.interview.core.application.usecases.hero.FindHeroUseCase;
import br.com.gubee.interview.core.application.usecases.hero.UpdateHeroUseCase;
import br.com.gubee.interview.core.application.usecases.hero.DeleteHeroUseCase;
import br.com.gubee.interview.core.presentation.controllers.HeroController;
import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.domain.enums.Race;
import br.com.gubee.interview.model.dto.request.CreateHeroRequest;
import br.com.gubee.interview.model.dto.request.UpdateHeroRequest;
import br.com.gubee.interview.model.exceptions.HeroNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HeroController.class)
class HeroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateHeroUseCase createHeroUseCase;

    @MockBean
    private FindHeroUseCase findHeroUseCase;

    @MockBean
    private UpdateHeroUseCase updateHeroUseCase;

    @MockBean
    private DeleteHeroUseCase deleteHeroUseCase;

    private UUID heroId;
    private HeroDTO heroDTO;
    private CreateHeroRequest createHeroRequest;
    private UpdateHeroRequest updateHeroRequest;

    @BeforeEach
    void setUp() {
        heroId = UUID.randomUUID();

        heroDTO = HeroDTO.builder()
                .id(heroId)
                .name("Superman")
                .race(Race.ALIEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .enabled(true)
                .strength(100)
                .agility(80)
                .dexterity(90)
                .intelligence(70)
                .build();

        createHeroRequest = CreateHeroRequest.builder()
                .name("Superman")
                .race(Race.ALIEN)
                .strength(100)
                .agility(80)
                .dexterity(90)
                .intelligence(70)
                .build();

        updateHeroRequest = UpdateHeroRequest.builder()
                .name("Superman Updated")
                .strength(95)
                .build();
    }

    @Test
    void findById_shouldReturnHeroWhenExists() throws Exception {
        when(findHeroUseCase.findById(heroId)).thenReturn(Optional.of(heroDTO));

        mockMvc.perform(get("/api/v1/heroes/{id}", heroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Herói encontrado com sucesso!")))
                .andExpect(jsonPath("$.data.id", is(heroId.toString())))
                .andExpect(jsonPath("$.data.name", is("Superman")))
                .andExpect(jsonPath("$.data.race", is("ALIEN")))
                .andExpect(jsonPath("$.data.strength", is(100)))
                .andExpect(jsonPath("$.data.agility", is(80)));

        verify(findHeroUseCase).findById(heroId);
    }

    @Test
    void findById_shouldReturnNotFoundWhenHeroDoesNotExist() throws Exception {
        when(findHeroUseCase.findById(heroId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/heroes/{id}", heroId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Herói não encontrado")))
                .andExpect(jsonPath("$.id", is(heroId.toString())));

        verify(findHeroUseCase).findById(heroId);
    }

    @Test
    void findById_shouldReturnBadRequestWhenInvalidUUID() throws Exception {
        mockMvc.perform(get("/api/v1/heroes/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("ID inválido. Formato esperado: UUID")))
                .andExpect(jsonPath("$.id", is("invalid-uuid")));

        verify(findHeroUseCase, never()).findById(any());
    }

    @Test
    void findByName_shouldReturnListOfHeroes() throws Exception {
        List<HeroDTO> heroes = Arrays.asList(heroDTO, heroDTO);
        when(findHeroUseCase.findByName("super")).thenReturn(heroes);

        mockMvc.perform(get("/api/v1/heroes")
                        .param("name", "super"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Heróis encontrados com sucesso!")))
                .andExpect(jsonPath("$.count", is(2)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("Superman")));

        verify(findHeroUseCase).findByName("super");
    }

    @Test
    void findByName_shouldReturnEmptyListMessage() throws Exception {
        when(findHeroUseCase.findByName("unknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/heroes")
                        .param("name", "unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Nenhum herói encontrado")))
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(findHeroUseCase).findByName("unknown");
    }

    @Test
    void update_shouldReturnSuccessResponse() throws Exception {
        doNothing().when(updateHeroUseCase).execute(eq(heroId), any(UpdateHeroRequest.class));

        mockMvc.perform(put("/api/v1/heroes/{id}", heroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateHeroRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Herói atualizado!")))
                .andExpect(jsonPath("$.id", is(heroId.toString())));

        verify(updateHeroUseCase).execute(eq(heroId), any(UpdateHeroRequest.class));
    }

    @Test
    void update_shouldReturnNotFoundWhenHeroNotFound() throws Exception {
        doThrow(new HeroNotFoundException(heroId))
                .when(updateHeroUseCase).execute(eq(heroId), any(UpdateHeroRequest.class));

        mockMvc.perform(put("/api/v1/heroes/{id}", heroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateHeroRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Herói não encontrado")))
                .andExpect(jsonPath("$.id", is(heroId.toString())));

        verify(updateHeroUseCase).execute(eq(heroId), any(UpdateHeroRequest.class));
    }

    @Test
    void delete_shouldReturnSuccessResponse() throws Exception {
        doNothing().when(deleteHeroUseCase).execute(heroId);

        mockMvc.perform(delete("/api/v1/heroes/{id}", heroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Herói deletado com sucesso!")))
                .andExpect(jsonPath("$.id", is(heroId.toString())));

        verify(deleteHeroUseCase).execute(heroId);
    }

    @Test
    void delete_shouldReturnNotFoundWhenHeroNotFound() throws Exception {
        doThrow(new HeroNotFoundException(heroId))
                .when(deleteHeroUseCase).execute(heroId);

        mockMvc.perform(delete("/api/v1/heroes/{id}", heroId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Herói não encontrado para exclusão")))
                .andExpect(jsonPath("$.id", is(heroId.toString())));

        verify(deleteHeroUseCase).execute(heroId);
    }

    @Test
    void create_shouldValidateRequest() throws Exception {
        CreateHeroRequest invalidRequest = CreateHeroRequest.builder()
                .race(Race.ALIEN)
                .build();

        mockMvc.perform(post("/api/v1/heroes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(createHeroUseCase, never()).execute(any());
    }
}