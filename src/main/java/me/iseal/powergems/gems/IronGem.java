package me.iseal.powergems.gems;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.TempDataManager;
import me.iseal.powergems.misc.Gem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class IronGem extends Gem {

    private final TempDataManager tdm = Main.getSingletonManager().tempDataManager;
    private final AttributeModifier armorModifier = new AttributeModifier(Main.getAttributeUUID(), "Iron Fortification",
            8, AttributeModifier.Operation.ADD_NUMBER);
    private final AttributeModifier toughnessModifier = new AttributeModifier(Main.getAttributeUUID(),
            "Iron Fortification", 4, AttributeModifier.Operation.ADD_NUMBER);
    private final AttributeModifier knockbackAttribute = new AttributeModifier(Main.getAttributeUUID(),
            "Iron Fortification - Knockback", 5, AttributeModifier.Operation.ADD_NUMBER);

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        plr.getWorld().spawnParticle(Particle.CRIT, plr.getLocation().add(0, 1, 0), 20);
        plr.setAbsorptionAmount(2 * level);
        AttributeInstance knockbackInstance = plr.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        try {
            knockbackInstance.addModifier(knockbackAttribute);
        } catch (IllegalArgumentException ex) {
            l.warning("[PowerGems] " + plr.getDisplayName()
                    + " used Iron Gem Shift while already having the modifiers, please report this to the developer");
        }
        plr.setVelocity(new Vector(0, 0, 0));
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            OfflinePlayer op = Bukkit.getOfflinePlayer(plr.getUniqueId());
            if (op.isOnline()) {
                ;
                plr.setAbsorptionAmount(0.0);
                knockbackInstance.removeModifier(knockbackAttribute);
            } else {
                tdm.ironRightLeft.add(op.getUniqueId());
            }
        }, 150 * level);
    }

    @Override
    protected void leftClick(Player plr) {
        Vector direction = plr.getEyeLocation().getDirection();
        for (int i = 0; i < 20 + (level * 2); i++) {
            Vector coneDirection = direction.clone().rotateAroundY(i * 20);
            Arrow sa = plr.launchProjectile(Arrow.class, coneDirection);
            sa.setBounce(true);
            sa.setDamage(level);
            sa.setVelocity(sa.getVelocity().multiply(level));
            sa.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            sa.getPersistentDataContainer().set(Main.getIsGemProjectileKey(), PersistentDataType.BOOLEAN, true);
        }
        for (int i = 0; i < 5 + level; i++) {
            Arrow sa = plr.launchProjectile(Arrow.class);
            sa.setBounce(true);
            sa.setDamage(level);
            sa.setVelocity(sa.getVelocity().multiply(level));
            sa.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }

    @Override
    protected void shiftClick(Player plr) {
        AttributeInstance armorAttribute = plr.getAttribute(Attribute.GENERIC_ARMOR);
        AttributeInstance toughnessAttribute = plr.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        try {
            armorAttribute.addModifier(armorModifier);
            toughnessAttribute.addModifier(toughnessModifier);
        } catch (IllegalArgumentException ex) {
            l.warning("[PowerGems] " + plr.getDisplayName()
                    + " used Iron Gem Shift while already having the modifiers, please report this to the developer");
        }
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            OfflinePlayer op = Bukkit.getOfflinePlayer(plr.getUniqueId());
            if (op.isOnline()) {
                armorAttribute.removeModifier(armorModifier);
                toughnessAttribute.removeModifier(toughnessModifier);
            } else {
                tdm.ironShiftLeft.add(op.getUniqueId());
            }
        }, 200);
    }

    public void removeShiftModifiers(Player plr) {
        AttributeInstance armorAttribute = plr.getAttribute(Attribute.GENERIC_ARMOR);
        AttributeInstance toughnessAttribute = plr.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        armorAttribute.removeModifier(armorModifier);
        toughnessAttribute.removeModifier(toughnessModifier);
    }

    public void removeRightModifiers(Player plr) {
        AttributeInstance knockbackInstance = plr.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        knockbackInstance.removeModifier(knockbackAttribute);
    }

}
