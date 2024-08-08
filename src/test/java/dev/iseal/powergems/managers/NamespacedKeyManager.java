package dev.iseal.powergems.managers;

import dev.iseal.powergems.PowerGems;
import org.bukkit.NamespacedKey;

import java.util.HashMap;

public class NamespacedKeyManager {

    private final HashMap<String, NamespacedKey> namespacedKeyLookupMap = new HashMap<>();

    private void addKeyOrIgnore(String name){
        if (namespacedKeyLookupMap.containsKey(name)){
            return;
        }
        namespacedKeyLookupMap.put(name, NamespacedKey.fromString(name, PowerGems.getPlugin()));
    }

    public NamespacedKey getKey(String name) {
        addKeyOrIgnore(name);
        return namespacedKeyLookupMap.get(name);
    }

}
