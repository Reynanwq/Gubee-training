package com.guilhermehermes.infrastructure.adapter.in.web;

import br.com.gubee.interview.application.hero.useCases.GetHeroesUseCase;
import br.com.gubee.interview.domain.model.Hero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v2/heroes", produces = APPLICATION_JSON_VALUE)
@Validated
public class GetHeroesController {

    @Autowired
    private final GetHeroesUseCase getHeroesUseCase;

    public GetHeroesController(GetHeroesUseCase getHeroesUseCase) {
        this.getHeroesUseCase = getHeroesUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Hero>> getHeroes() {
        var output = getHeroesUseCase.execute();
        return ResponseEntity.ok().body(output);
    }



}
