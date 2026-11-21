package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.core.players.PlayerData;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicPlayerLoadedEvent extends Event {
   @Getter
   private final Player player;
   @Getter
   private final PlayerData playerData;
   private static final HandlerList handlers = new HandlerList();
   private boolean canceled = false;

   public MythicPlayerLoadedEvent(Player player, PlayerData playerData) {
      super(false);
      this.player = player;
      this.playerData = playerData;
   }

   public void setCancelled(boolean bool) {
      this.canceled = bool;
   }

   public boolean isCancelled() {
      return this.canceled;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
