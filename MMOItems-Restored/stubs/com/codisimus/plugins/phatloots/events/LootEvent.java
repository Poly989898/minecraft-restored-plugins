package com.codisimus.plugins.phatloots.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class LootEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public List<ItemStack> getItemList() { return null; }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
