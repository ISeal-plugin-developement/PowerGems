package dev.iseal.powergems.misc.WrapperObjects;

import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GemCacheItem {
    private final ArrayList<ItemStack> ownedGems;
    private final long insertionTime = System.currentTimeMillis();

    public GemCacheItem(ArrayList<ItemStack> ownedGems) {
        this.ownedGems = ownedGems;
    }

    public ArrayList<ItemStack> getOwnedGems() {
        return ownedGems;
    }

    public boolean isExpired() {
        // 1 minute
        return System.currentTimeMillis() - insertionTime > SingletonManager.gemCacheExpireTime;
    }
}
