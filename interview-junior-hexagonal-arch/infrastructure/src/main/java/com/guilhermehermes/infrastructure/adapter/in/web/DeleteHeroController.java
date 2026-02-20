package com.guilhermehermes.infrastructure.adapter.in.web;

import br.com.gubee.interview.application.hero.useCases.DeleteHeroUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v2/heroes", produces = APPLICATION_JSON_VALUE)
@Validated
public class DeleteHeroController {

    @Autowired
    private final DeleteHeroUseCase deleteHeroUseCase;

    public DeleteHeroController(DeleteHeroUseCase deleteHeroUseCase) {
        this.deleteHeroUseCase = deleteHeroUseCase;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHero(@PathVariable String id) {
        deleteHeroUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
