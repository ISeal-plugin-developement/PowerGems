package me.iseal.powergems.managers;

import me.iseal.powergems.Main;
import org.bukkit.NamespacedKey;

import java.util.HashMap;

public class NamespacedKeyManager {

    private final HashMap<String, NamespacedKey> namespacedKeyLookupMap = new HashMap<>();

    private void addKeyOrIgnore(String name){
        if (namespacedKeyLookupMap.containsKey(name)){
            return;
        }
        namespacedKeyLookupMap.put(name, NamespacedKey.fromString(name, Main.getPlugin()));
    }

    public NamespacedKey getKey(String name) {
        addKeyOrIgnore(name);
        return namespacedKeyLookupMap.get(name);
    }

}
