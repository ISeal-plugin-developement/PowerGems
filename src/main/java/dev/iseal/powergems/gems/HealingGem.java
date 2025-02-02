package dev.iseal.powergems.gems;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.Configuration.GemLoreConfigManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.meta.ItemMeta;
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

    @Override
    public ItemStack gemInfo(ItemStack item) {
        // Use base gem info which includes level and cooldowns
        ItemStack infoItem = super.gemInfo(item);
        ItemMeta meta = infoItem.getItemMeta();
        ArrayList<String> lore = new ArrayList<>(meta.getLore());
        
        // Add configured lore from GemLoreConfigManager
        lore.addAll(sm.configManager.getRegisteredConfigInstance(GemLoreConfigManager.class).getLore(GemManager.lookUpID("Healing")));
        
        meta.setLore(lore);
        infoItem.setItemMeta(meta);
        return infoItem;
    }
}
