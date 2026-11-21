package me.frep.vulcan.api.event;

import me.frep.vulcan.api.check.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/** Compile-only stub (not packaged). */
public class VulcanFlagEvent extends Event implements Cancellable {
   public Check getCheck() { return null; }
   public Player getPlayer() { return null; }
   public boolean isCancelled() { return false; }
   public void setCancelled(boolean cancel) {}
   public HandlerList getHandlers() { return null; }
}
