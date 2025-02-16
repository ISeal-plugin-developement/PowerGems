package dev.iseal.powergems.listeners.passivePowerListeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

public class StrengthPermaEffect {

    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final List<String> allowedGems = List.of("Strength");
    public static final ArrayList<UUID> hasGemRaining = new ArrayList<>();

    private static final int EFFECT_DURATION = 100; 
    private static final int CHECK_PERIOD = 100;   

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

    public StrengthPermaEffect() {
        PowerGems.getPlugin().getServer().getScheduler()
            .runTaskTimer(PowerGems.getPlugin(), (task) -> {
                for (Player plr : PowerGems.getPlugin().getServer().getOnlinePlayers()) {
                    if (playerHasAllowedGem(plr)) {
                        PotionEffect existing = plr.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                        if (existing == null) {
                            plr.addPotionEffect(new PotionEffect(
                                PotionEffectType.INCREASE_DAMAGE,
                                EFFECT_DURATION,
                                0,
                                false,
                                false
                            ));
                        }
                    } else if (plr.getPotionEffect(PotionEffectType.INCREASE_DAMAGE) != null && 
                            plr.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() == 0) {
                        plr.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                    }
                }
            }, 0, CHECK_PERIOD);
    }
}