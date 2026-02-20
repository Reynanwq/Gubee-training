package br.com.gubee.interview.domain.adapters;

import br.com.gubee.interview.domain.model.Hero;

public interface SaveHeroPort {
    Hero save(Hero hero);
    Hero update(Hero hero);
}
