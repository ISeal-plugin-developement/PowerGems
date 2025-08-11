package dev.iseal.powergems.gems;

import java.util.ArrayList;
import java.util.logging.Level;

import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.TempDataManager;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealUtils.utils.ExceptionHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class IronGem extends Gem {

    public IronGem() {
        super("Iron");
    }

    private final TempDataManager tdm = SingletonManager.getInstance().tempDataManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    private final AttributeModifier armorModifier = new AttributeModifier(
            new NamespacedKey("powergems", "iron_fortification_armor"),
            8,
            AttributeModifier.Operation.ADD_NUMBER
    );
    private final AttributeModifier toughnessModifier = new AttributeModifier(
            new NamespacedKey("powergems", "iron_fortification_toughness"),
            4,
            AttributeModifier.Operation.ADD_NUMBER
    );
    private final AttributeModifier knockbackAttribute = new AttributeModifier(
            new NamespacedKey("powergems", "iron_fortification_knockback"),
            5,
            AttributeModifier.Operation.ADD_NUMBER
    );

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        plr.getWorld().spawnParticle(Particle.CRIT, plr.getLocation().add(0, 1, 0), 20);
        plr.setAbsorptionAmount(2 * level);

        AttributeInstance knockbackInstance = plr.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        if (knockbackInstance != null) {
            try {
                knockbackInstance.addModifier(knockbackAttribute);
            } catch (IllegalArgumentException ex) {
                ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "ALREADY_HAS_IRON_MODIFIERS_RIGHT", plr.getName());
            }
        }
        plr.setVelocity(new Vector(0, 0, 0));

        schedulerWrapper.scheduleDelayedTaskForEntity(plr, () -> {
            OfflinePlayer op = Bukkit.getOfflinePlayer(plr.getUniqueId());
            if (op.isOnline()) {
                plr.setAbsorptionAmount(0.0);
                if (knockbackInstance != null) {
                    knockbackInstance.removeModifier(knockbackAttribute);
                }
            } else {
                tdm.ironRightLeft.add(op.getUniqueId());
            }
        }, 150L * level, null);
    }

    @Override
    protected void leftClick(Player plr, int level) {
        Vector direction = plr.getEyeLocation().getDirection();
        for (int i = 0; i < 20 + (level * 2); i++) {
            Vector coneDirection = direction.clone().rotateAroundY(i * 20);
            Arrow sa = plr.launchProjectile(Arrow.class, coneDirection);
            sa.setHasBeenShot(false);
            sa.setDamage(level);
            sa.setVelocity(sa.getVelocity().multiply(level));
            sa.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            sa.getPersistentDataContainer().set(nkm.getKey("is_gem_projectile"), PersistentDataType.BOOLEAN, true);
            sa.getPersistentDataContainer().set(nkm.getKey("gem_owner"), PersistentDataType.STRING, "Iron");
        }
        for (int i = 0; i < 5 + level; i++) {
            Arrow sa = plr.launchProjectile(Arrow.class);
            sa.setHasBeenShot(false);
            sa.setDamage(level);
            sa.setVelocity(sa.getVelocity().multiply(level));
            sa.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        AttributeInstance armorAttribute = plr.getAttribute(Attribute.ARMOR);
        AttributeInstance toughnessAttribute = plr.getAttribute(Attribute.ARMOR_TOUGHNESS);

        if (armorAttribute != null && toughnessAttribute != null) {
            try {
                armorAttribute.addModifier(armorModifier);
                toughnessAttribute.addModifier(toughnessModifier);
            } catch (IllegalArgumentException ex) {
                ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "ALREADY_HAS_IRON_MODIFIERS_SHIFT", plr.getName());
            }
        }

        schedulerWrapper.scheduleDelayedTaskForEntity(plr, () -> {
            OfflinePlayer op = Bukkit.getOfflinePlayer(plr.getUniqueId());
            if (op.isOnline()) {
                if (armorAttribute != null) {
                    armorAttribute.removeModifier(armorModifier);
                }
                if (toughnessAttribute != null) {
                    toughnessAttribute.removeModifier(toughnessModifier);
                }
            } else {
                tdm.ironShiftLeft.add(op.getUniqueId());
            }
        }, 200, null);
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.RESISTANCE; // Updated for Paper 1.21+
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
        lore.add(Component.text("Right click: Temporarily grants the player increased absorption and knockback resistance.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Shift click: Temporarily increases the player's armor and armor toughness.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Left click: Fires a barrage of spectral arrows in a circle shape.", NamedTextColor.WHITE).toString());
        return lore;
    }

    public void removeShiftModifiers(Player plr) {
        AttributeInstance armorAttribute = plr.getAttribute(Attribute.ARMOR);
        AttributeInstance toughnessAttribute = plr.getAttribute(Attribute.ARMOR_TOUGHNESS);
        if (armorAttribute != null) {
            armorAttribute.removeModifier(armorModifier);
        }
        if (toughnessAttribute != null) {
            toughnessAttribute.removeModifier(toughnessModifier);
        }
    }

    public void removeRightModifiers(Player plr) {
        AttributeInstance knockbackInstance = plr.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        if (knockbackInstance != null) {
            knockbackInstance.removeModifier(knockbackAttribute);
        }
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.CRIT;
    }
}
