package com.guilhermehermes.infrastructure.adapter.in.web;

import br.com.gubee.interview.application.hero.useCases.CompareHeroesUseCase;
import br.com.gubee.interview.application.hero.service.compare_heroes.CompareHeroesInput;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v2/heroes", produces = APPLICATION_JSON_VALUE)
@Validated
public class CompareHeroesController {

    @Autowired
    private final CompareHeroesUseCase compareHeroesUseCase;

    public CompareHeroesController(CompareHeroesUseCase compareHeroesUseCase) {
        this.compareHeroesUseCase = compareHeroesUseCase;
    }


    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> createHero(@RequestBody @Valid CompareHeroesInput input) {
        var output = compareHeroesUseCase.execute(input);
        URI location = URI.create("/api/v2/heroes/" + output.get("id"));
        return ResponseEntity.created(location).body(output);
    }
}
