package br.com.gubee.interview.core.response;

import br.com.gubee.interview.model.enums.Race;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

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
