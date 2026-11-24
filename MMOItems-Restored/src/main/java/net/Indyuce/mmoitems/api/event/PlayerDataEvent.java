package net.Indyuce.mmoitems.api.event;

import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

public abstract class PlayerDataEvent extends PlayerEvent implements Cancellable {
   private final PlayerData playerData;
   private boolean cancelled;

   public PlayerDataEvent(PlayerData playerData) {
      super(playerData.getPlayer());
      this.playerData = playerData;
   }

   public PlayerData getPlayerData() {
      return this.playerData;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean flag) {
      this.cancelled = flag;
   }
}
