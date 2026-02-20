package br.com.gubee.interview.application.hero.service.create_hero;

import br.com.gubee.interview.domain.model.Hero;
import br.com.gubee.interview.domain.enums.Race;
import br.com.gubee.interview.domain.vo.PowerStats;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import javax.validation.constraints.NotNull;

public class CreateHeroInput {

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Race cannot be null")
    private Race race;

    @NotNull(message = "Power stats cannot be null")
    @JsonProperty("power_stats")
    @Valid
    private PowerStats powerStats;

    @NotNull(message = "Enabled status must be specified")
    private Boolean enabled;

    public CreateHeroInput() {}

    public CreateHeroInput(String name, Race race, PowerStats powerStats, Boolean enabled) {
        this.name = name;
        this.race = race;
        this.powerStats = powerStats;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public PowerStats getPowerStats() {
        return powerStats;
    }

    public void setPowerStats(PowerStats powerStats) {
        this.powerStats = powerStats;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Hero toHero() {
        return new Hero(null, this.name, this.race, this.powerStats, this.enabled);
    }
}