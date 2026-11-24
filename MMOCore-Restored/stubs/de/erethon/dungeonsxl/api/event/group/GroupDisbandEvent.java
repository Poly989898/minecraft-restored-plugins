package de.erethon.dungeonsxl.api.event.group;

import de.erethon.dungeonsxl.api.player.PlayerGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GroupDisbandEvent extends Event {
    public PlayerGroup getGroup() { throw new UnsupportedOperationException("compile-only stub"); }
    @Override public HandlerList getHandlers() { throw new UnsupportedOperationException("compile-only stub"); }
    public static HandlerList getHandlerList() { throw new UnsupportedOperationException("compile-only stub"); }
}
