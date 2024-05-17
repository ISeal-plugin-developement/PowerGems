package me.iseal.powergems.listeners.passivePowerListeners;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.GemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

public class damageListener implements Listener {

    private final GemManager gm = Main.getSingletonManager().gemManager;
    private final ArrayList<String> noFall = new ArrayList<>(Arrays.asList("Air", "Lightning"));

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player plr = (Player) e.getEntity();
        checkIfFall(plr, e);
    }

    private void checkIfFall(Player p, EntityDamageEvent event){
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) 
            return;
        for (ItemStack i : gm.getPlayerGems(p)){
            if (noFall.contains(i.getItemMeta().getPersistentDataContainer().get(Main.getGemPowerKey(), PersistentDataType.STRING))){
                event.setCancelled(true);
                return;
            }
        }
    }
}
