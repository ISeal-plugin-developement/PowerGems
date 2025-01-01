package dev.iseal.powergems.gems;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.listeners.FallingBlockHitListener;
import dev.iseal.powergems.listeners.powerListeners.IceTargetListener;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.I18N.I18N;
import dev.iseal.sealLib.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IceGem extends Gem {

    public IceGem() {
        super("Ice");
    }

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
        fb.setVelocity(plr.getLocation().getDirection());
        fb.getVelocity().multiply((level * 5) + 1);
        fbhl.addEntityUUID(fb.getUniqueId());
    }

    @Override
    protected void leftClick(Player plr) {
        int distance = 15 + level * 5; // Maximum distance between the players
        LivingEntity ent = GlobalUtils.raycastInaccurate(plr, distance);
        if (ent == null) {
            plr.sendMessage(I18N.getTranslation("MUST_LOOK_AT_PLAYER"));
            return;
        }

        ent.setFreezeTicks(100 + (level * 2) * 20);
        ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100 + (level * 2) * 20, level - 1));
    }

    @Override
    protected void shiftClick(Player plr) {
        Location l = plr.getLocation();
        World w = plr.getWorld();
        for (int i = 0; i < level * 2; i++) {
            w.spawnEntity(l, EntityType.SNOWMAN);
        }
        itl.addToList(plr);

        Bukkit.getScheduler().scheduleSyncDelayedTask(PowerGems.getPlugin(), () -> itl.removeFromList(plr), 1200);
    }
}
