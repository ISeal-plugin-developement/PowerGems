package dev.iseal.powergems.gems;

import dev.iseal.powergems.gems.powerClasses.StrenghArena;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.managers.Configuration.GemLoreConfigManager;
import dev.iseal.powergems.managers.GemManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class StrengthGem extends Gem {

    public StrengthGem() {
        super("Strength");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    public ItemStack gemInfo(ItemStack item) {
        // Use base gem info which includes level and cooldowns
        ItemStack infoItem = super.gemInfo(item);
        ItemMeta meta = infoItem.getItemMeta();
        ArrayList<String> lore = new ArrayList<>(meta.getLore());
        
        // Add configured lore from GemLoreConfigManager
        lore.addAll(sm.configManager.getRegisteredConfigInstance(GemLoreConfigManager.class).getLore(GemManager.lookUpID("Strength")));
        
        meta.setLore(lore);
        infoItem.setItemMeta(meta);
        return infoItem;
    }

    @Override
    protected void rightClick(Player plr) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 1));
    }

    @Override
    protected void leftClick(Player plr) {
        double distance = 10;
        double power = 2 + (level / 2);
        Location playerLocation = plr.getLocation();
        List<Entity> nearbyEntities = plr.getNearbyEntities(distance, distance, distance);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player nearbyPlayer && ! ((Player) entity).equals(plr)) {
                Vector knockbackVector = nearbyPlayer.getLocation().subtract(playerLocation).toVector();
                nearbyPlayer.setVelocity(knockbackVector.multiply(power));
                nearbyPlayer.damage(5);
                nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2));
            }
        }
    }

    @Override
    protected void shiftClick(Player plr) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2));
        new StrenghArena(plr).start();
    }
}
