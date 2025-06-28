package dev.iseal.powergems.managers.Configuration;

import com.google.common.base.Enums;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.sealUtils.utils.ExceptionHandler;
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
        String gemName = GemManager.lookUpName(i);
        if (file.contains(gemName + "GemParticle")) return;
        if (i == -1) ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid gem ID: " + i), Level.WARNING, "CREATE_DEFAULT_PARTICLE_SETTINGS");
        file.set(
                gemName+"GemParticle",
                GemReflectionManager.getInstance().getSingletonGemInstance(gemName).getDefaultParticle().name()
        );
    }

    public Particle getParticle(int gemID) {
        if (CACHE.containsKey(gemID)) return CACHE.get(gemID);
        CACHE.put(gemID,Enums.getIfPresent(Particle.class, file.getString(GemManager.lookUpName(gemID) + "GemParticle"))
                        .or(() -> {
                            ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid particle type: " + file.getString(GemManager.lookUpName(gemID) + "GemParticle")), Level.WARNING, "GET_PARTICLE", CACHE.toString());
                            return Particle.DAMAGE_INDICATOR;
                        })
        );
        return CACHE.get(gemID);
    }

}
