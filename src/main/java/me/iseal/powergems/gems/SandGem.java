package me.iseal.powergems.gems;

import me.iseal.powergems.Main;
import me.iseal.powergems.misc.Gem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

public class SandGem extends Gem {

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        RayTraceResult result = plr.getWorld().rayTrace(plr.getEyeLocation(), plr.getEyeLocation().getDirection(), 200D,
                FluidCollisionMode.ALWAYS, true, 1, entity -> !entity.equals(plr) && entity instanceof Player);
        if (result == null || result.getHitEntity() == null) {
            plr.sendMessage(ChatColor.DARK_RED + "You need to aim at a player to do that");
            return;
        }
        Player target = (Player) result.getHitEntity();
        if (target == null) {
            plr.sendMessage(ChatColor.DARK_RED + "You must be looking at a player to do that");
            return;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80 + (level * 40), 1 + level));
        target.setFoodLevel(Math.max(0, target.getFoodLevel() - 5 + (level * 2)));
    }

    @Override
    protected void leftClick(Player plr) {
        RayTraceResult result = plr.getWorld().rayTrace(plr.getEyeLocation(), plr.getEyeLocation().getDirection(),
                10 + level, FluidCollisionMode.ALWAYS, true, 1,
                entity -> !entity.equals(plr) && entity instanceof Player);
        if (result == null || result.getHitEntity() == null) {
            plr.sendMessage(ChatColor.DARK_RED + "You need to aim at a player to do that");
            return;
        }
        Player target = (Player) result.getHitEntity();
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60 + (level * 40), 1 + level));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60 + (level * 40), 1 + level));
    }

    @Override
    protected void shiftClick(Player plr) {
        Block possibleTarget = plr.getTargetBlock(null, 90);
        if (possibleTarget == null) {
            plr.sendMessage(ChatColor.DARK_RED + "You must be looking at a block to do that");
            return;
        }
        Material oldMaterial = possibleTarget.getType();
        Location targetLocation = possibleTarget.getLocation();
        targetLocation.getBlock().setType(Material.SAND);
        sm.sandMoveListen.addToList(possibleTarget);

        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            targetLocation.getBlock().setType(oldMaterial);
            sm.sandMoveListen.removeFromList(possibleTarget);
        }, 200L);
    }
}
