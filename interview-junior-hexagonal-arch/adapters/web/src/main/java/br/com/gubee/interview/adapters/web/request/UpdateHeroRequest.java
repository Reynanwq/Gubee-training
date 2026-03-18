package br.com.gubee.interview.model.dto.request;

import br.com.gubee.interview.model.domain.enums.Race;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHeroRequest {
    private String name;
    private Race race;
    private Integer strength;
    private Integer agility;
    private Integer dexterity;
    private Integer intelligence;
}