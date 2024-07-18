package me.iseal.powergems.gems;

import me.iseal.powergems.gems.powerClasses.StrenghArena;
import me.iseal.powergems.misc.Gem;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class StrengthGem extends Gem {

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 300, 1));
    }

    @Override
    protected void leftClick(Player plr) {
        double distance = 10;
        double power = 2 + (level / 2);
        Location playerLocation = plr.getLocation();
        List<Entity> nearbyEntities = plr.getNearbyEntities(distance, distance, distance);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player && !((Player) entity).equals(plr)) {
                Player nearbyPlayer = (Player) entity;
                Vector knockbackVector = nearbyPlayer.getLocation().subtract(playerLocation).toVector();
                nearbyPlayer.setVelocity(knockbackVector.multiply(power));
                nearbyPlayer.damage(5);
                nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2));
            }
        }
    }

    @Override
    protected void shiftClick(Player plr) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 2));
        new StrenghArena(plr).start();
    }
}
