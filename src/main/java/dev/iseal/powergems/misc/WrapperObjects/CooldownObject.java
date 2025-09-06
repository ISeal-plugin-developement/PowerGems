package dev.iseal.powergems.misc.WrapperObjects;

import org.bukkit.entity.Player;

public class CooldownObject {

    private final Player plr;
    private final Class<?> fromClass;
    private final long time;

    public CooldownObject(Player plr, Class<?> fromClass, long time) {
        this.plr = plr;
        this.fromClass = fromClass;
        this.time = time;
    }

    public Player getPlayer() {
        return plr;
    }

    public Class<?> getFromClass() {
        return fromClass;
    }

    public long getTime() {
        return time;
    }

}
