package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.exception.HeroNotFoundException;
import br.com.gubee.interview.core.response.*;
import br.com.gubee.interview.core.response.UpdateHeroRequest;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/heroes", produces = APPLICATION_JSON_VALUE)
public class HeroController {

    private final HeroService heroService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<HeroResponse> create(@Validated @RequestBody CreateHeroRequest createHeroRequest) {
        final UUID id = heroService.create(createHeroRequest);

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
            Optional<Hero> heroOptional = heroService.findById(uuid);

            if (heroOptional.isPresent()) {
                HeroFindResponse response = HeroFindResponse.builder()
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
    public ResponseEntity<HeroListResponse> findByName(@RequestParam String name) {
        List<Hero> heroes = heroService.findByName(name);

        HeroListResponse response = HeroListResponse.builder()
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
            heroService.update(id, updates);
            HeroUpdateResponse response = HeroUpdateResponse.builder()
                    .message("Herói atualizado com sucesso!")
                    .id(id)
                    .build();
            return ResponseEntity.ok(response);
        } catch (HeroNotFoundException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message("Herói não encontrado para atualização")
                    .id(id.toString())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            heroService.delete(id);
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