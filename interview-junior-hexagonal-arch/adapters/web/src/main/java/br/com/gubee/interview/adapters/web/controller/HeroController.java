package br.com.gubee.interview.core.presentation.controllers;

import br.com.gubee.interview.core.application.usecases.hero.CreateHeroUseCase;
import br.com.gubee.interview.core.application.usecases.hero.DeleteHeroUseCase;
import br.com.gubee.interview.core.application.usecases.hero.FindHeroUseCase;
import br.com.gubee.interview.core.application.usecases.hero.UpdateHeroUseCase;
import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.dto.request.CreateHeroRequest;
import br.com.gubee.interview.model.dto.request.UpdateHeroRequest;
import br.com.gubee.interview.model.dto.response.ErrorResponse;
import br.com.gubee.interview.model.dto.response.HeroFindResponse;
import br.com.gubee.interview.model.dto.response.HeroListResponse;
import br.com.gubee.interview.model.dto.response.HeroResponse;
import br.com.gubee.interview.model.dto.response.HeroUpdateResponse;
import br.com.gubee.interview.model.exceptions.HeroNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/heroes", produces = APPLICATION_JSON_VALUE)
public class HeroController {

    private final CreateHeroUseCase createHeroUseCase;
    private final FindHeroUseCase findHeroUseCase;
    private final UpdateHeroUseCase updateHeroUseCase;
    private final DeleteHeroUseCase deleteHeroUseCase;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<HeroResponse> create(@Validated @RequestBody CreateHeroRequest createHeroRequest) {
        final UUID id = createHeroUseCase.execute(createHeroRequest);

        HeroResponse response = HeroResponse.builder()
                .message("Herói cadastrado com sucesso!")
                .id(id)
                .location(format("/api/v1/heroes/%s", id))
                .build();

        return created(URI.create(format("/api/v1/heroes/%s", id)))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<HeroDTO> heroOptional = findHeroUseCase.findById(uuid);

            if (heroOptional.isPresent()) {
                HeroFindResponse<HeroDTO> response = HeroFindResponse.<HeroDTO>builder()
                        .message("Herói encontrado com sucesso!")
                        .data(heroOptional.get())
                        .build();
                return ResponseEntity.ok(response);
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .message("Herói não encontrado")
                        .id(id)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message("ID inválido. Formato esperado: UUID")
                    .id(id)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<HeroListResponse<HeroDTO>> findByName(@RequestParam String name) {
        List<HeroDTO> heroes = findHeroUseCase.findByName(name);

        HeroListResponse<HeroDTO> response = HeroListResponse.<HeroDTO>builder()
                .message(heroes.isEmpty() ? "Nenhum herói encontrado" : "Heróis encontrados com sucesso!")
                .count(heroes.size())
                .data(heroes)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @Validated @RequestBody UpdateHeroRequest updates) {

        try {
            updateHeroUseCase.execute(id, updates);
            HeroUpdateResponse response = HeroUpdateResponse.builder()
                    .message("Herói atualizado!")
                    .id(id)
                    .build();
            return ResponseEntity.ok(response);
        } catch (HeroNotFoundException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message("Herói não encontrado")
                    .id(id.toString())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            deleteHeroUseCase.execute(id);
            HeroUpdateResponse response = HeroUpdateResponse.builder()
                    .message("Herói deletado com sucesso!")
                    .id(id)
                    .build();
            return ResponseEntity.ok(response);
        } catch (HeroNotFoundException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message("Herói não encontrado para exclusão")
                    .id(id.toString())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}