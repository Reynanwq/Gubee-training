package br.com.gubee.interview.domain.model;

import br.com.gubee.interview.domain.enums.Race;
import br.com.gubee.interview.domain.vo.PowerStats;
import org.bson.types.ObjectId;


import java.io.Serializable;
import java.util.Objects;

public class Hero implements Serializable {

    private static final long serialVersionUID = 1L;

    private ObjectId id;

    private String name;

    private Race race;

    private Boolean enabled;

    private PowerStats powerStats;


    public Hero() {
    }

    public Hero(ObjectId id, String name, Race race, PowerStats powerStats, Boolean enabled) {
        this.id = id;
        this.name = name;
        this.race = race;
        this.powerStats = powerStats;
        this.enabled = enabled;
    }


    public String getId() {
        return id != null ? id.toString() : null;
    }

    public void setId( ObjectId id) {
        this.id = id;
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

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hero hero = (Hero) o;
        return Objects.equals(id, hero.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Hero{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", race=" + race +
                ", powerStats='" + powerStats + '\'' +
                ", enabled=" + enabled +
                '}';
    }



}
