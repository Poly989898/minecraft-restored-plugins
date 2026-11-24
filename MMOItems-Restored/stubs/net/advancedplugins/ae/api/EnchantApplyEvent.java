package net.advancedplugins.ae.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Cancellable;

public class EnchantApplyEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    public ItemStack getItem() { return null; }
    public boolean isCancelled() { return false; }
    public void setCancelled(boolean cancel) { }
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
