package net.Indyuce.mmocore.api.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.cast.PlayerKey;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerKeyPressEvent extends PlayerDataEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Cancellable cancellable;
   private final PlayerKey pressed;

   public PlayerKeyPressEvent(PlayerData playerData, PlayerKey pressed, Cancellable cancellable) {
      super(playerData);
      this.pressed = pressed;
      this.cancellable = cancellable;
   }

   public PlayerKey getPressed() {
      return this.pressed;
   }

   public boolean isCancelled() {
      return this.cancellable.isCancelled();
   }

   public void setCancelled(boolean b) {
      this.cancellable.setCancelled(b);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
