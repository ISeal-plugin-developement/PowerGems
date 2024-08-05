package me.iseal.powergems.gems;

import me.iseal.powergems.misc.Gem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HealingGem extends Gem {

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, level - 1));
    }

    @Override
    protected void leftClick(Player plr) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, level - 1));
    }

    @Override
    protected void shiftClick(Player plr) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, level));
        plr.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200, level / 2));
    }
}
