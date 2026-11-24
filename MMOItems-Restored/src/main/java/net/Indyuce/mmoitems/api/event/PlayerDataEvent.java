package net.Indyuce.mmoitems.api.event;

import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

public abstract class PlayerDataEvent extends PlayerEvent implements Cancellable {
   private final PlayerData playerData;
   private boolean cancelled;

   public PlayerDataEvent(PlayerData var1) {
      super(var1.getPlayer());
      this.playerData = var1;
   }

   public PlayerData getPlayerData() {
      return this.playerData;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }
}
