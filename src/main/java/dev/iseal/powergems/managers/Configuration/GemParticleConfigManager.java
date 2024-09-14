package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import org.bukkit.Particle;

import java.util.HashMap;

public class GemParticleConfigManager extends AbstractConfigManager {

    private final HashMap<Integer,Particle> CACHE = new HashMap<>();

    public GemParticleConfigManager() {
        super("GemParticles");
    }

    @Override
    public void setUpConfig() {
        for (int i = 1; i < GemManager.TOTAL_GEM_AMOUNT; i++) {
            createDefaultParticleSettings(i);
        }
    }

    private void createDefaultParticleSettings(int i) {
        if (file.contains("Gem" + GemManager.lookUpName(i) + "Particle")) return;
        Particle setParticle = switch (i) {
            case 1 -> Particle.DAMAGE_INDICATOR;
            case 2 -> Particle.HEART;
            case 3 -> Particle.CLOUD;
            case 4 -> Particle.LAVA;
            case 5 -> Particle.CRIT;
            case 6 -> Particle.CRIT_MAGIC;
            case 7 -> Particle.FIREWORKS_SPARK;
            case 8 -> Particle.SNOWFLAKE;
            case 9 -> Particle.DRIP_LAVA;
            case 10 -> Particle.DRIP_WATER;
            default -> null;
        };
        file.set("Gem" + GemManager.lookUpName(i) + "Particle", setParticle.name());
    }

    public Particle getParticle(int gemID) {
        if (CACHE.containsKey(gemID)) return CACHE.get(gemID);
        CACHE.put(gemID, Particle.valueOf(file.getString("Gem" + GemManager.lookUpName(gemID) + "Particle")));
        return CACHE.get(gemID);
    }

}
