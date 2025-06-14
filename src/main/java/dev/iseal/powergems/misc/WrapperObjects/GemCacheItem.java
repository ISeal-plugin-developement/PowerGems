package dev.iseal.powergems.misc.WrapperObjects;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealUtils.utils.GlobalUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class GemCacheItem {
    private final ArrayList<ItemStack> ownedGems;
    private final long insertionTime = System.currentTimeMillis();
    private boolean dirty = false;

    public GemCacheItem(ArrayList<ItemStack> ownedGems) {
        this.ownedGems = ownedGems;
    }

    public ArrayList<ItemStack> getOwnedGems() {
        return ownedGems;
    }

    private boolean isExpired() {
        return System.currentTimeMillis() - insertionTime > SingletonManager.gemCacheExpireTime* 1000L;
    }

    private void checkDirty() {
        dirty = !GlobalUtils.areListsSimilar(
                ownedGems.stream()
                        .filter(Objects::nonNull)
                        .filter(ItemStack::hasItemMeta)
                        .filter(item -> GemManager.getInstance().isGem(item))
                        .toList(),
                ownedGems.stream()
                        .toList()
        );
    }

    private boolean isDirty() {
        checkDirty();
        return dirty;
    }

    public boolean isValid() {
        return !isExpired() && !isDirty();
    }
}
