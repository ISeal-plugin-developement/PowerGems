package dev.iseal.powergems.managers.Configuration;

import com.google.common.base.Enums;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.powergems.misc.ExceptionHandler;
import org.bukkit.Particle;

import java.util.HashMap;
import java.util.logging.Level;

public class GemParticleConfigManager extends AbstractConfigManager {

    private final HashMap<Integer,Particle> CACHE = new HashMap<>();

    public GemParticleConfigManager() {
        super("GemParticles");
    }

    @Override
    public void setUpConfig() {

    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            createDefaultParticleSettings(i);
        }
    }

    private void createDefaultParticleSettings(int i) {
        if (file.contains("Gem" + GemManager.lookUpName(i) + "Particle")) return;
        if (i == -1) ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid gem ID: " + i), Level.WARNING, "CREATE_DEFAULT_PARTICLE_SETTINGS");
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
            default -> Particle.VILLAGER_ANGRY;
        };
        file.set("Gem" + GemManager.lookUpName(i) + "Particle", setParticle.name());
    }

    public Particle getParticle(int gemID) {
        if (CACHE.containsKey(gemID)) return CACHE.get(gemID);
        CACHE.put(gemID,Enums.getIfPresent(Particle.class, file.getString("Gem" + GemManager.lookUpName(gemID) + "Particle"))
                        .or(() -> {
                            ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid particle type: " + file.getString("Gem" + GemManager.lookUpName(gemID) + "Particle")), Level.WARNING, "GET_PARTICLE", CACHE.toString());
                            return Particle.DAMAGE_INDICATOR;
                        })
        );
        return CACHE.get(gemID);
    }

}
