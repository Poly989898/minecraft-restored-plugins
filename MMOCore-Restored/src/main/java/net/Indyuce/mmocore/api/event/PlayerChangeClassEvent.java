package net.Indyuce.mmocore.api.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerChangeClassEvent extends PlayerDataEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final PlayerClass newClass;
   private boolean cancelled = false;

   public PlayerChangeClassEvent(PlayerData player, PlayerClass newClass) {
      super(player);
      this.newClass = newClass;
   }

   public PlayerClass getNewClass() {
      return this.newClass;
   }

   public boolean isSubclass() {
      return this.getData().getProfess().getSubclasses().stream().anyMatch(sub -> sub.getProfess().equals(this.newClass));
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean value) {
      this.cancelled = value;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
