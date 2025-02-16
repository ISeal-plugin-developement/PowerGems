package dev.iseal.powergems.listeners.passivePowerListeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;

public class FirePermaEffect {
    private static final int EFFECT_DURATION = 100; 
    private static final int CHECK_PERIOD = 100;    
    
    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final List<String> allowedGems = List.of("Fire");

    private boolean playerHasAllowedGem(Player plr) {
        return gm.getPlayerGems(plr).stream()
                .filter(item -> item != null)
                .filter(ItemStack::hasItemMeta)
                .anyMatch(item -> {
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) return false;
                    String gemPower = meta.getPersistentDataContainer()
                            .get(nkm.getKey("gem_power"), PersistentDataType.STRING);
                    return gemPower != null && allowedGems.contains(gemPower);
                });
    }

    public FirePermaEffect() {
        PowerGems.getPlugin().getServer().getScheduler().runTaskTimer(
            PowerGems.getPlugin(), 
            (task) -> {
                for (Player plr : PowerGems.getPlugin().getServer().getOnlinePlayers()) {
                    if (playerHasAllowedGem(plr)) {
                        PotionEffect existing = plr.getPotionEffect(PotionEffectType.FIRE_RESISTANCE);
                        if (existing == null) {
                            plr.addPotionEffect(new PotionEffect(
                                PotionEffectType.FIRE_RESISTANCE,
                                EFFECT_DURATION,  
                                0,    
                                false, 
                                false  
                            ));
                        }
                    } else if (plr.getPotionEffect(PotionEffectType.FIRE_RESISTANCE) != null && 
                        plr.getPotionEffect(PotionEffectType.FIRE_RESISTANCE).getAmplifier() == 0) {
                        plr.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                    }
                }
            },
            0L,  
            CHECK_PERIOD
        );
    }
}
