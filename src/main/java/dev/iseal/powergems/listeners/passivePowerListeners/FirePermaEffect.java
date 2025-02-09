package dev.iseal.powergems.listeners.passivePowerListeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;

public class FirePermaEffect implements Listener {

    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final List<String> allowedGems = List.of("Fire");
    public static final ArrayList<UUID> hasGemRaining = new ArrayList<>();

    private void applyEffect(Player plr, int duration, int amplifier) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, amplifier));
    }

    private boolean playerHasAllowedGem(Player plr) {
        return gm.getPlayerGems(plr).stream()
                .anyMatch(i -> allowedGems.contains(i.getItemMeta()
                        .getPersistentDataContainer().get(nkm.getKey("gem_power"), PersistentDataType.STRING)));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player plr = event.getPlayer();
        if (playerHasAllowedGem(plr)) {
            applyEffect(plr, 100, 1);
        }
    }
}