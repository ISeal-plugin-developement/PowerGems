package dev.iseal.powergems.gems;

import dev.iseal.powergems.gems.powerClasses.StrenghArena;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
<<<<<<< HEAD
=======

>>>>>>> 8bedf562cce08fed7e5d5f10fb00550cd5ef0461
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
    protected void rightClick(Player plr, int level) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 1));
        plr.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,300 , 1)); 
    }

    @Override
    protected void leftClick(Player plr, int level) {
        double distance = 10;
        double power = 2 + ((double) level / 2);
        Location playerLocation = plr.getLocation();
        List<Entity> nearbyEntities = plr.getNearbyEntities(distance, distance, distance);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player nearbyPlayer && ! entity.equals(plr)) {
                Vector knockbackVector = nearbyPlayer.getLocation().subtract(playerLocation).toVector();
                nearbyPlayer.setVelocity(knockbackVector.multiply(power));
                nearbyPlayer.damage(5);
                nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2));
            }
        }
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2));
        new StrenghArena(plr).start();
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.INCREASE_DAMAGE;
    }
}
