package dev.iseal.powergems.gems;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Utils.ExceptionHandler;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class IronGem extends Gem {

    public IronGem() {
        super("Iron");
    }

    private final TempDataManager tdm = SingletonManager.getInstance().tempDataManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final AttributeModifier armorModifier = new AttributeModifier(PowerGems.getAttributeUUID(),
            "Iron Fortification", 8, AttributeModifier.Operation.ADD_NUMBER);
    private final AttributeModifier toughnessModifier = new AttributeModifier(PowerGems.getAttributeUUID(),
            "Iron Fortification", 4, AttributeModifier.Operation.ADD_NUMBER);
    private final AttributeModifier knockbackAttribute = new AttributeModifier(PowerGems.getAttributeUUID(),
            "Iron Fortification - Knockback", 5, AttributeModifier.Operation.ADD_NUMBER);

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        plr.getWorld().spawnParticle(Particle.CRIT, plr.getLocation().add(0, 1, 0), 20);
        plr.setAbsorptionAmount(2 * level);
        AttributeInstance knockbackInstance = plr.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        try {
            knockbackInstance.addModifier(knockbackAttribute);
        } catch (IllegalArgumentException ex) {
            ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "ALREADY_HAS_IRON_MODIFIERS_RIGHT", plr.getName());
        }
        plr.setVelocity(new Vector(0, 0, 0));
        Bukkit.getScheduler().runTaskLater(PowerGems.getPlugin(), () -> {
            OfflinePlayer op = Bukkit.getOfflinePlayer(plr.getUniqueId());
            if (op.isOnline()) {
                plr.setAbsorptionAmount(0.0);
                knockbackInstance.removeModifier(knockbackAttribute);
            } else {
                tdm.ironRightLeft.add(op.getUniqueId());
            }
        }, 150L * level);
    }

    @Override
    protected void leftClick(Player plr, int level) {
        Vector direction = plr.getEyeLocation().getDirection();
        for (int i = 0; i < 20 + (level * 2); i++) {
            Vector coneDirection = direction.clone().rotateAroundY(i * 20);
            Arrow sa = plr.launchProjectile(Arrow.class, coneDirection);
            sa.setBounce(true);
            sa.setDamage(level);
            sa.setVelocity(sa.getVelocity().multiply(level));
            sa.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            sa.getPersistentDataContainer().set(nkm.getKey("is_gem_projectile"), PersistentDataType.BOOLEAN, true);
            sa.getPersistentDataContainer().set(nkm.getKey("gem_owner"), PersistentDataType.STRING, "Iron");
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
    protected void shiftClick(Player plr, int level) {
        AttributeInstance armorAttribute = plr.getAttribute(Attribute.GENERIC_ARMOR);
        AttributeInstance toughnessAttribute = plr.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        try {
            armorAttribute.addModifier(armorModifier);
            toughnessAttribute.addModifier(toughnessModifier);
        } catch (IllegalArgumentException ex) {
            ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "ALREADY_HAS_IRON_MODIFIERS_SHIFT", plr.getName());
        }
        Bukkit.getScheduler().runTaskLater(PowerGems.getPlugin(), () -> {
            OfflinePlayer op = Bukkit.getOfflinePlayer(plr.getUniqueId());
            if (op.isOnline()) {
                armorAttribute.removeModifier(armorModifier);
                toughnessAttribute.removeModifier(toughnessModifier);
            } else {
                tdm.ironShiftLeft.add(op.getUniqueId());
            }
        }, 200);
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.DAMAGE_RESISTANCE;
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
