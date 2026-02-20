package com.guilhermehermes.infrastructure.adapter.in.web;

import br.com.gubee.interview.application.hero.useCases.CreateHeroUseCase;
import br.com.gubee.interview.application.hero.service.create_hero.CreateHeroInput;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v2/heroes", produces = APPLICATION_JSON_VALUE)
@Validated
public class CreateHeroController {

    private final CreateHeroUseCase createHeroUseCase;

    public CreateHeroController(CreateHeroUseCase createHeroUseCase) {
        this.createHeroUseCase = createHeroUseCase;
    }


    @PostMapping
    public ResponseEntity<Map<String, Object>> createHero(@RequestBody @Valid CreateHeroInput input) {
        var output = createHeroUseCase.execute(input);
        URI location = URI.create("/api/v2/heroes/" + output.get("id"));
        return ResponseEntity.created(location).body(output);
    }
}
