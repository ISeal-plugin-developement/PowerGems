package me.iseal.powergems.gems;

import me.iseal.powergems.Main;
import me.iseal.powergems.listeners.powerListeners.lavaTargetListener;
import me.iseal.powergems.misc.Gem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static me.iseal.powergems.Main.config;


@SuppressWarnings("unchecked")
public class LavaGem extends Gem {

    private lavaTargetListener ltl = new lavaTargetListener();
    private ArrayList<Material> blockedBlocks;
    {
        try {
            blockedBlocks = (ArrayList<Material>) config.getList("blockedLavaBlocks");
        } catch (ClassCastException e){
            Bukkit.getLogger().warning("Config file shaped incorrectly, resorting to default state");
            blockedBlocks = new ArrayList<>();
        }
    }

    @Override
    public void call(Action act, Player plr, ItemStack item){
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        plr.sendMessage(ChatColor.DARK_RED+"This ability has been temporarily disabled.");
        /*int radius = 5;
        int times = (level/2)+1;
        HashMap<Block, Material> toChangeBack = new HashMap<>();

        while (times != 0) {
            ArrayList<Block> blocks = u.getSquareOutlineCoordinates(plr, radius);
            for (Block block : blocks) {
                if (blockedBlocks.contains(block.getType())){
                    continue;
                }
                toChangeBack.put(block, block.getType());
                block.setType(Material.LAVA);
            }
            times--;
            radius = radius+3;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (Block b : toChangeBack.keySet()){
                    b.setType(toChangeBack.get(b));
                }
            }
        }, 1200L);*/
    }

    @Override
    protected void leftClick(Player plr) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0));
    }

    @Override
    protected void shiftClick(Player plr) {
        plr.getWorld().spawnEntity(plr.getLocation(), EntityType.BLAZE);
        ltl.addToList(plr);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ltl.removeFromList(plr);
            }
        }, 2400L);
    }
}
