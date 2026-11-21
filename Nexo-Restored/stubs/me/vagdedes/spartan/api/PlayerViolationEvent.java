package me.vagdedes.spartan.api;

import me.vagdedes.spartan.system.Enums;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/** Compile-only stub (not packaged). */
public class PlayerViolationEvent extends Event implements Cancellable {
   public Enums.HackType getHackType() { return null; }
   public Player getPlayer() { return null; }
   public boolean isCancelled() { return false; }
   public void setCancelled(boolean cancel) {}
   public HandlerList getHandlers() { return null; }
}
