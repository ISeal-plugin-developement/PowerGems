package dev.iseal.powergems.gems;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.listeners.AvoidTargetListener;
import dev.iseal.powergems.listeners.FallingBlockHitListener;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import dev.iseal.sealLib.Systems.I18N.I18N;
import dev.iseal.sealLib.Utils.SpigotGlobalUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Comparator;

public class IceGem extends Gem {
    private final FallingBlockHitListener fbhl = sm.fallingBlockHitListen;
    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    public IceGem() {
        super("Ice");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location l = plr.getEyeLocation();
            FallingBlock fb = l.getWorld().spawn(l, FallingBlock.class, fallingBlock -> {
                fallingBlock.setBlockData(Material.ICE.createBlockData());
                fallingBlock.setHurtEntities(true);
                fallingBlock.setDamagePerBlock(level);
            });
            fb.setVelocity(plr.getLocation().getDirection().multiply(level * 5 + 1));
            fbhl.addEntityUUID(fb.getUniqueId());
        });
    }

    @Override
    protected void leftClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            int distance = 15 + level * 5;
            LivingEntity ent = SpigotGlobalUtils.raycastInaccurate(plr, distance);
            if (ent == null) {
                plr.sendMessage(I18N.translate("MUST_LOOK_AT_PLAYER"));
                return;
            }

            ent.setFreezeTicks(100 + (level * 2) * 20);
            ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100 + (level * 2) * 20, level - 1));
        });
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location l = plr.getLocation();
            World w = plr.getWorld();

            // Pre-create NamespacedKey to avoid creating multiple instances
            NamespacedKey ownerKey = new NamespacedKey(PowerGems.getPlugin(), "OWNER_NAME");
            NamespacedKey damageKey = new NamespacedKey(PowerGems.getPlugin(), "SNOWBALL_DAMAGE");

            for (int i = 0; i < level * 2; i++) {
                Snowman golem = (Snowman) w.spawnEntity(l, EntityType.SNOW_GOLEM);

                // Configure snowman properties
                golem.customName(Component.text(I18N.translate("OWNED_SNOW_GOLEM").replace("{owner}", plr.getName())));
                golem.setCustomNameVisible(true);
                golem.setHealth(Math.min(i + 2, 4.0));
                golem.setDerp(true);  // More accurate throwing
                golem.setTarget(null);
                golem.setAware(true);

                // Set targeting parameters
                golem.setTarget(getNearestHostilePlayer(plr, golem));

                // Set PDC data
                PersistentDataContainer pdc = golem.getPersistentDataContainer();
                pdc.set(ownerKey, PersistentDataType.STRING, plr.getName());
                pdc.set(damageKey, PersistentDataType.DOUBLE, 2.0 * level);

                // Add to avoid target list and schedule removal after 300 seconds (6000 ticks)
                AvoidTargetListener.getInstance().addToList(plr, golem, 6000);
            }
        });
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.REGENERATION;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Component.text("Level %level%", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Abilities", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Right click: Launches an ice projectile that damages entities on impact.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Left click: Freezes and slows the target entity for a duration based on gem level.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Shift click: Spawns snow golems to fight for you with increased health and damage.", NamedTextColor.WHITE).toString());
        return lore;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.ITEM_SNOWBALL;}

    private Player getNearestHostilePlayer(Player owner, Snowman golem) {
        return golem.getWorld().getNearbyEntities(golem.getLocation(), 10.0, 10.0, 10.0).stream()
            .filter(entity -> entity instanceof Player)  // Filter for players
            .map(entity -> (Player) entity)             // Cast to Player
            .filter(player -> !player.equals(owner))    // Exclude owner
            .filter(player -> !player.isDead())         // Only target living players
            .min(Comparator.comparingDouble(player -> 
                player.getLocation().distanceSquared(golem.getLocation())))
            .orElse(null);
    }
}
