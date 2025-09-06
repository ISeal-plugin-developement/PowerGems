package dev.iseal.powergems.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class HealingGem extends Gem {

    public HealingGem() {
        super("Healing");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 9));
    }

    @Override
    protected void leftClick(Player plr, int level) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, level - 1));
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, level));
        plr.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200, level / 2));
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
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Parry");
        lore.add(ChatColor.WHITE + "Shift click: Instant heal");
        lore.add(ChatColor.WHITE + "Left click: 1 minute of regeneration 2");
        return lore;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.HEART;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
