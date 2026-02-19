package br.com.gubee.interview.core.presentation.mappers;

import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.dto.request.CreateHeroRequest;
import br.com.gubee.interview.model.domain.entities.PowerStats;
import org.springframework.stereotype.Component;

@Component
public class PowerStatsDtoMapper {

    public PowerStats toDomain(CreateHeroRequest request) {
        if (request == null) {
            return null;
        }

        return PowerStats.builder()
                .strength(request.getStrength())
                .agility(request.getAgility())
                .dexterity(request.getDexterity())
                .intelligence(request.getIntelligence())
                .build();
    }

    public void updatePowerStatsFromDTO(HeroDTO heroDTO, PowerStats powerStats) {
        if (heroDTO == null || powerStats == null) {
            return;
        }

        powerStats.setStrength(heroDTO.getStrength());
        powerStats.setAgility(heroDTO.getAgility());
        powerStats.setDexterity(heroDTO.getDexterity());
        powerStats.setIntelligence(heroDTO.getIntelligence());
    }

    public PowerStats toDomain(HeroDTO heroDTO) {
        if (heroDTO == null) {
            return null;
        }

        return PowerStats.builder()
                .strength(heroDTO.getStrength())
                .agility(heroDTO.getAgility())
                .dexterity(heroDTO.getDexterity())
                .intelligence(heroDTO.getIntelligence())
                .build();
    }
}