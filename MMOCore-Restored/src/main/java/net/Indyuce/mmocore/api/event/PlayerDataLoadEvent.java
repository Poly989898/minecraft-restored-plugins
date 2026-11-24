package net.Indyuce.mmocore.api.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.event.HandlerList;

@Deprecated
public class PlayerDataLoadEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();

   public PlayerDataLoadEvent(PlayerData playerData) {
      super(playerData);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
