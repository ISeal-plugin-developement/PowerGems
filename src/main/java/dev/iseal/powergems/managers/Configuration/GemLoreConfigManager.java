package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.List;

public class GemLoreConfigManager extends AbstractConfigManager {

    private static final TagResolver tagResolver = TagResolver.standard();
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public GemLoreConfigManager() {
        super("GemLore");
    }

    @Override
    public void setUpConfig() {

    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            createDefaultLore(GemManager.lookUpName(i));
        }
    }

    public void createDefaultLore(String gemName) {
        if (file.contains("Gem" + gemName + "Lore"))
            return;
        file.set(
                "Gem"+gemName+"Lore",
                GemReflectionManager.getInstance().getSingletonGemInstance(gemName).getDefaultLore()
        );

    }

    public List<Component> getLore(int gemNumber) {
        return getLore(gemNumber, tagResolver);
    }

    public List<Component> getLore(int gemNumber, TagResolver inTagResolver) {
        // build the component list
        List<Component> lore = new ArrayList<>();
        file.getStringList("Gem" + GemManager.lookUpName(gemNumber) + "Lore").forEach(
                line -> lore.add(miniMessage.deserialize(line, tagResolver, inTagResolver))
        );
        return lore;
    }

}
