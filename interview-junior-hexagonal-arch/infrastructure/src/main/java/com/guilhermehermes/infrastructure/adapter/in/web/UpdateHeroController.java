package com.guilhermehermes.infrastructure.adapter.in.web;

import br.com.gubee.interview.application.hero.useCases.UpdateHeroUseCase;
import br.com.gubee.interview.application.hero.service.update_hero.UpdateHeroInput;
import br.com.gubee.interview.domain.model.Hero;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v2/heroes", produces = APPLICATION_JSON_VALUE)
@Validated
public class UpdateHeroController {

    @Autowired
    private final UpdateHeroUseCase updateHeroUseCase;

    public UpdateHeroController(UpdateHeroUseCase updateHeroUseCase) {
        this.updateHeroUseCase = updateHeroUseCase;
    }


    @PutMapping
    public ResponseEntity<Hero> createHero(@RequestBody @Valid UpdateHeroInput input) {
        var output = updateHeroUseCase.execute(input);
        URI location = URI.create("/api/v2/heroes/" + output.getId());
        return ResponseEntity.ok().location(location).body(output);
    }
}
