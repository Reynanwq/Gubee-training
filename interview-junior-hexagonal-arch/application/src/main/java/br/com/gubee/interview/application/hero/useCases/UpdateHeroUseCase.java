package br.com.gubee.interview.application.hero.useCases;

import br.com.gubee.interview.application.hero.service.update_hero.UpdateHeroInput;
import br.com.gubee.interview.domain.model.Hero;

public interface UpdateHeroUseCase {

    public Hero execute(UpdateHeroInput input);

}
