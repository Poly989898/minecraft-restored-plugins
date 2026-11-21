package io.lumine.mythic.bukkit.utils.storage.events;

import io.lumine.mythic.bukkit.utils.storage.players.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LumineProfileLoadEvent<P extends Profile> extends Event {
   private final Player player;
   private final P profile;
   private static final HandlerList handlers = new HandlerList();

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public LumineProfileLoadEvent(Player player, P profile) {
      this.player = player;
      this.profile = profile;
   }

   public Player getPlayer() {
      return this.player;
   }

   public P getProfile() {
      return this.profile;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof LumineProfileLoadEvent<?> other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$player = this.getPlayer();
         Object other$player = other.getPlayer();
         if (this$player == null ? other$player == null : this$player.equals(other$player)) {
            Object this$profile = this.getProfile();
            Object other$profile = other.getProfile();
            return this$profile == null ? other$profile == null : this$profile.equals(other$profile);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof LumineProfileLoadEvent;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $player = this.getPlayer();
      result = result * 59 + ($player == null ? 43 : $player.hashCode());
      Object $profile = this.getProfile();
      return result * 59 + ($profile == null ? 43 : $profile.hashCode());
   }

   public String toString() {
      return "LumineProfileLoadEvent(player=" + this.getPlayer() + ", profile=" + this.getProfile() + ")";
   }
}
