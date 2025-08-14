package dev.iseal.powergems.gems;

import java.util.ArrayList;
import java.util.Comparator;

import dev.iseal.sealLib.Utils.SpigotGlobalUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.listeners.AvoidTargetListener;
import dev.iseal.powergems.listeners.FallingBlockHitListener;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;

public class IceGem extends Gem {
    private final FallingBlockHitListener fbhl = sm.fallingBlockHitListen;

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
        Location l = plr.getEyeLocation();
        FallingBlock fb = l.getWorld().spawnFallingBlock(l, Material.ICE.createBlockData());
        fb.setHurtEntities(true);
        fb.setDamagePerBlock(level);
        fb.setVelocity(plr.getLocation().getDirection());
        fb.getVelocity().multiply(level * 5 + 1);
        fbhl.addEntityUUID(fb.getUniqueId());
    }

    @Override
    protected void leftClick(Player plr, int level) {
        int distance = 15 + level * 5;
        LivingEntity ent = SpigotGlobalUtils.raycastInaccurate(plr, distance);
        if (ent == null) {
            plr.sendMessage(I18N.translate("MUST_LOOK_AT_PLAYER"));
            return;
        }

        ent.setFreezeTicks(100 + (level * 2) * 20);
        ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100 + (level * 2) * 20, level - 1));
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        Location l = plr.getLocation();
        World w = plr.getWorld();
        
        // Pre-create NamespacedKey to avoid creating multiple instances
        NamespacedKey ownerKey = new NamespacedKey(PowerGems.getPlugin(), "OWNER_NAME");
        NamespacedKey damageKey = new NamespacedKey(PowerGems.getPlugin(), "SNOWBALL_DAMAGE");
        
        for (int i = 0; i < level * 2; i++) {
            Snowman golem = (Snowman) w.spawnEntity(l, EntityType.SNOWMAN);
            
            // Configure snowman properties
            golem.setCustomName(I18N.translate("OWNED_SNOW_GOLEM").replace("{owner}", plr.getName()));
            golem.setCustomNameVisible(true);
            golem.setHealth(Math.min(i + 2, 4.0));
            golem.setDerp(true);  // More accurate throwing
            golem.setTarget(null);
            golem.setAware(true);
            
            // Set targeting parameters
            golem.setTarget(getNearestHostilePlayer(plr, golem, 10.0));
            
            // Set PDC data
            PersistentDataContainer pdc = golem.getPersistentDataContainer();
            pdc.set(ownerKey, PersistentDataType.STRING, plr.getName());
            pdc.set(damageKey, PersistentDataType.DOUBLE, 2.0 * level);

            // Add to avoid target list and schedule removal after 300 seconds (6000 ticks)
            AvoidTargetListener.getInstance().addToList(plr, golem);
        }
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.HEAL;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Throw an ice block, dealing damage to whoever gets hit");
        lore.add(ChatColor.WHITE + "Shift click: Spawns snow golems to fight for you");
        lore.add(ChatColor.WHITE + "Left click: Freezes the player you aim giving him slowness");
        return lore;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.SNOW_SHOVEL;
    }

    private Player getNearestHostilePlayer(Player owner, Snowman golem, double range) {
        return golem.getWorld().getNearbyEntities(golem.getLocation(), range, range, range).stream()
            .filter(entity -> entity instanceof Player)  // Filter for players
            .map(entity -> (Player) entity)             // Cast to Player
            .filter(player -> !player.equals(owner))    // Exclude owner
            .filter(player -> !player.isDead())         // Only target living players
            .min(Comparator.comparingDouble(player -> 
                player.getLocation().distanceSquared(golem.getLocation())))
            .orElse(null);
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
