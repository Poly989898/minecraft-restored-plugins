package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class MythicPlayerAttackEvent extends Event {
   private final AbstractPlayer player;
   private final AbstractEntity target;
   private static final HandlerList handlers = new HandlerList();
   private boolean canceled = false;

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

   @Generated
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MythicPlayerAttackEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isCanceled() != other.isCanceled()) {
         return false;
      } else {
         Object this$player = this.getPlayer();
         Object other$player = other.getPlayer();
         if (this$player == null ? other$player == null : this$player.equals(other$player)) {
            Object this$target = this.getTarget();
            Object other$target = other.getTarget();
            return this$target == null ? other$target == null : this$target.equals(other$target);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof MythicPlayerAttackEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isCanceled() ? 79 : 97);
      Object $player = this.getPlayer();
      result = result * 59 + ($player == null ? 43 : $player.hashCode());
      Object $target = this.getTarget();
      return result * 59 + ($target == null ? 43 : $target.hashCode());
   }
}
