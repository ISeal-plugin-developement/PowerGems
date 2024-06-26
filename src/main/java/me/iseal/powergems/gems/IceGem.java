package me.iseal.powergems.gems;

import me.iseal.powergems.Main;
import me.iseal.powergems.listeners.FallingBlockHitListener;
import me.iseal.powergems.listeners.powerListeners.IceTargetListener;
import me.iseal.powergems.misc.Gem;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

public class IceGem extends Gem {

    private final IceTargetListener itl = sm.iceTargetListen;
    private final FallingBlockHitListener fbhl = sm.fallingBlockHitListen;

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        Location l = plr.getEyeLocation();
        FallingBlock fb = l.getWorld().spawnFallingBlock(l, Material.ICE.createBlockData());
        fb.setHurtEntities(true);
        fb.setDamagePerBlock(level);
        fb.setVelocity(l.getDirection());
        fb.getVelocity().multiply((level * 5) + 1);
        fbhl.addEntityUUID(fb.getUniqueId());
    }

    @Override
    protected void leftClick(Player plr) {
        int distance = 15 + level * 5; // Maximum distance between the players
        RayTraceResult result = plr.getWorld().rayTrace(plr.getEyeLocation(), plr.getEyeLocation().getDirection(),
                distance, FluidCollisionMode.ALWAYS, true, 1,
                entity -> !entity.equals(plr) && entity instanceof Player);
        if (result == null || result.getHitEntity() == null) {
            plr.sendMessage(ChatColor.DARK_RED + "You need to aim at a player to do that");
            return;
        }
        Player targetplr = (Player) result.getHitEntity();
        targetplr.setFreezeTicks(100 + (level * 2) * 20);
        targetplr.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100 + (level * 2) * 20, level - 1));
    }

    @Override
    protected void shiftClick(Player plr) {
        Location l = plr.getLocation();
        World w = plr.getWorld();
        for (int i = 0; i < level * 2; i++) {
            w.spawnEntity(l, EntityType.SNOWMAN);
        }
        itl.addToList(plr);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                itl.removeFromList(plr);
            }
        }, 1200);
    }
}
