package com.evill4mer.RealDualWield.Api;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
public class PlayerDamageEntityWithOffhandEvent extends Event {
    public Player getPlayer() { return null; }
    public Entity getEntity() { return null; }
    public double getDamage() { return 0; }
    public HandlerList getHandlers() { return null; }
}
