package com.archyx.aureliumskills.data;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDataLoadEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public PlayerData getPlayerData() { return null; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
