package br.com.gubee.interview.core.application.usecases.powerstats;

import br.com.gubee.interview.model.domain.entities.PowerStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CalculatePowerStatsUseCase {

    /**
     * Calcula a média dos poderes
     */
    public double calculateAverage(PowerStats powerStats) {
        if (powerStats == null) return 0;

        int sum = powerStats.getStrength()
                + powerStats.getAgility()
                + powerStats.getDexterity()
                + powerStats.getIntelligence();

        return sum / 4.0;
    }

    /**
     * Retorna o poder mais alto
     */
    public int getHighestPower(PowerStats powerStats) {
        if (powerStats == null) return 0;

        return Math.max(
                Math.max(powerStats.getStrength(), powerStats.getAgility()),
                Math.max(powerStats.getDexterity(), powerStats.getIntelligence())
        );
    }

    /**
     * Retorna o poder mais baixo
     */
    public int getLowestPower(PowerStats powerStats) {
        if (powerStats == null) return 0;

        return Math.min(
                Math.min(powerStats.getStrength(), powerStats.getAgility()),
                Math.min(powerStats.getDexterity(), powerStats.getIntelligence())
        );
    }

    /**
     * Calcula o poder total (soma de todos)
     */
    public int getTotalPower(PowerStats powerStats) {
        if (powerStats == null) return 0;

        return powerStats.getStrength()
                + powerStats.getAgility()
                + powerStats.getDexterity()
                + powerStats.getIntelligence();
    }

    /**
     * Retorna um mapa com estatísticas detalhadas
     */
    public Map<String, Object> getDetailedStats(PowerStats powerStats) {
        Map<String, Object> stats = new HashMap<>();

        if (powerStats == null) {
            stats.put("error", "PowerStats não encontrado");
            return stats;
        }

        stats.put("strength", powerStats.getStrength());
        stats.put("agility", powerStats.getAgility());
        stats.put("dexterity", powerStats.getDexterity());
        stats.put("intelligence", powerStats.getIntelligence());
        stats.put("average", calculateAverage(powerStats));
        stats.put("highest", getHighestPower(powerStats));
        stats.put("lowest", getLowestPower(powerStats));
        stats.put("total", getTotalPower(powerStats));

        return stats;
    }

    /**
     * Compara dois PowerStats e retorna qual é mais forte
     */
    public PowerStats compare(PowerStats ps1, PowerStats ps2) {
        if (ps1 == null) return ps2;
        if (ps2 == null) return ps1;

        int total1 = getTotalPower(ps1);
        int total2 = getTotalPower(ps2);

        return total1 >= total2 ? ps1 : ps2;
    }
}