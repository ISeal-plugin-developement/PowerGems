package dev.iseal.powergems.gems;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import dev.iseal.sealLib.Systems.I18N.I18N;
import net.kyori.adventure.text.Component;
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
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            if (plr.getEyeLocation().getBlock().getType() != Material.WATER
                    || plr.getLocation().getBlock().getType() != Material.WATER)
                return;
            plr.setVelocity(plr.getVelocity().add(plr.getLocation().getDirection().multiply(level / 2)));
            World world = plr.getWorld();
            world.spawnParticle(Particle.BUBBLE_COLUMN_UP, plr.getLocation(), 5 * level);
        });
    }

    @Override
    protected void leftClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location loc = plr.getLocation();
            loc.setY(loc.getY() - 1);
            int halfRadius = level * 2;

            // Process farmland blocks around the player
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
        });
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        // Disable shift click in the nether
        if(plr.getWorld().getEnvironment() == World.Environment.NETHER) {
            plr.sendMessage(I18N.translate("WATER_GEM_SHIFT_DISABLED_NETHER"));
            return;
        }
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location playerLocation = plr.getLocation();
            int radius = 3 + level;

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    for (int y = -1; y <= 2; y++) {
                        Location blockLocation = playerLocation.clone().add(x, y, z);
                        Block block = blockLocation.getBlock();
                        if (!block.isEmpty())
                            continue;
                        block.setType(Material.WATER);
                        schedulerWrapper.scheduleDelayedTaskAtLocation(blockLocation, () -> {
                            if (block.getType() == Material.WATER)
                                block.setType(Material.AIR);
                        }, 400 + level * 40L);
                    }
                }
            }
            plr.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 500 + level * 100, 2));
        });
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Component.text("Level %level%", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Abilities", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Right click: Propel yourself forward in water, creating bubbles.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Shift click: Create a temporary water cube around you, granting Dolphin's Grace.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Left click: Moisturize farmland blocks around you.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Passive: Power up yourself with water", NamedTextColor.BLUE).toString());
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
