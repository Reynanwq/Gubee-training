package br.com.gubee.interview.core.presentation.mappers;

import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.dto.request.CreateHeroRequest;
import br.com.gubee.interview.model.dto.response.HeroFindResponse;
import br.com.gubee.interview.model.dto.response.HeroResponse;
import br.com.gubee.interview.model.domain.entities.Hero;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class HeroDtoMapper {

    public Hero toDomain(CreateHeroRequest request) {
        if (request == null) {
            return null;
        }

        return Hero.builder()
                .name(request.getName())
                .race(request.getRace())
                .enabled(true)
                .build();
    }

    public HeroDTO toDTO(Hero hero, PowerStats powerStats) {
        if (hero == null || powerStats == null) {
            return null;
        }

        return HeroDTO.builder()
                .id(hero.getId())
                .name(hero.getName())
                .race(hero.getRace())
                .enabled(hero.isEnabled())
                .createdAt(hero.getCreatedAt())
                .updatedAt(hero.getUpdatedAt())
                .strength(powerStats.getStrength())
                .agility(powerStats.getAgility())
                .dexterity(powerStats.getDexterity())
                .intelligence(powerStats.getIntelligence())
                .build();
    }

    public List<HeroDTO> toDTOList(List<Hero> heroes, List<PowerStats> powerStatsList) {
        if (heroes == null || powerStatsList == null || heroes.size() != powerStatsList.size()) {
            return new ArrayList<>();
        }

        List<HeroDTO> result = new ArrayList<>();
        for (int i = 0; i < heroes.size(); i++) {
            result.add(toDTO(heroes.get(i), powerStatsList.get(i)));
        }
        return result;
    }

    public HeroResponse toResponse(UUID id) {
        return HeroResponse.builder()
                .message("Herói cadastrado com sucesso!")
                .location("/api/v1/heroes/" + id)
                .build();
    }

    public HeroFindResponse<HeroDTO> toFindResponse(HeroDTO heroDTO) {
        return HeroFindResponse.<HeroDTO>builder()
                .message("Herói encontrado com sucesso!")
                .data(heroDTO)
                .build();
    }
}