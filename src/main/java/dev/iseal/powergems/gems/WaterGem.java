package dev.iseal.powergems.gems;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import dev.iseal.sealLib.Systems.I18N.I18N;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class WaterGem extends Gem {
    //TODO: This class requires Folia integration
    public WaterGem() {
        super("Water");
    }

    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        if (plr.getEyeLocation().getBlock().getType() != Material.WATER
                || plr.getLocation().getBlock().getType() != Material.WATER)
            return;
        plr.setVelocity(plr.getVelocity().add(plr.getLocation().getDirection().multiply(level / 2)));
        World world = plr.getWorld();
        world.spawnParticle(Particle.BUBBLE_COLUMN_UP, plr.getLocation(), 5 * level);
    }

    @Override
    protected void leftClick(Player plr, int level) {
        Location loc = plr.getLocation();
        loc.setY(loc.getY() - 1);
        int halfRadius = level * 2;
        // Itinerate in a square around the location
        for (int x = -halfRadius; x <= halfRadius; x++) {
            for (int z = -halfRadius; z <= halfRadius; z++) {
                Location pos = new Location(loc.getWorld(), loc.getX() + x, loc.getY(), loc.getZ() + z);
                Block block = pos.getBlock();
                if (block.getType() != Material.FARMLAND)
                    continue;
                Farmland farmland = (Farmland) block.getBlockData();
                farmland.setMoisture(farmland.getMaximumMoisture());
                block.setBlockData(farmland);
                block.applyBoneMeal(BlockFace.UP);
            }
        }
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        //Disable shift click in the nether
        if(plr.getWorld().getEnvironment() == World.Environment.NETHER) {
            plr.sendMessage(I18N.translate("WATER_GEM_SHIFT_DISABLED_NETHER"));
            return;
        }
        // Get the player's positionLocation playerLocation = plr.getLocation();
        int radius = 3 + level;
        Location playerLocation = plr.getLocation();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -1; y <= 2; y++) {
                    Location blockLocation = playerLocation.clone().add(x, y, z);
                    Block block = blockLocation.getBlock();
                    if (!block.isEmpty())
                        continue;
                    block.setType(Material.WATER);

                    // Use SchedulerWrapper for Folia compatibility - region scheduler since it's location-specific
                   schedulerWrapper.runTaskLaterAtLocation(blockLocation, () -> {
                        if (block.getType() == Material.WATER)
                            block.setType(Material.AIR);
                    }, 400+level* 40L);
                }
            }
        }
        plr.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 500+level*100, 2));
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Propel yourself forward in water, creating bubbles.");
        lore.add(ChatColor.WHITE + "Shift click: Create a temporary water cube around you, granting Dolphin's Grace.");
        lore.add(ChatColor.WHITE + "Left click: Moisturize farmland blocks around you.");
        lore.add(ChatColor.BLUE + "Passive: Power up yourself with water");
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.CONDUIT_POWER;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.BUBBLE;
    }
}
