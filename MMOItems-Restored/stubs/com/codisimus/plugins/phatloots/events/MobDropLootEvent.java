package com.codisimus.plugins.phatloots.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobDropLootEvent extends LootEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
