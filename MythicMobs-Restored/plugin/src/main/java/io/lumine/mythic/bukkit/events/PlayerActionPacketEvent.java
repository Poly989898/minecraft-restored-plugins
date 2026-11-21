package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.adapters.AbstractDirection;
import io.lumine.mythic.api.adapters.AbstractLocation;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
@ToString
public class PlayerActionPacketEvent extends Event implements Cancellable {
   private final Player player;
   private final PlayerActionPacketEvent.Action action;
   private final AbstractLocation location;
   private final AbstractDirection direction;
   private boolean cancelled = false;
   private static final HandlerList handlers = new HandlerList();

   public PlayerActionPacketEvent(Player player, PlayerActionPacketEvent.Action action, AbstractLocation location, AbstractDirection direction) {
      super(true);
      this.player = player;
      this.action = action;
      this.location = location;
      this.direction = direction;
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
      } else if (!(o instanceof PlayerActionPacketEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isCancelled() != other.isCancelled()) {
         return false;
      } else {
         Object this$player = this.getPlayer();
         Object other$player = other.getPlayer();
         if (this$player == null ? other$player == null : this$player.equals(other$player)) {
            Object this$action = this.getAction();
            Object other$action = other.getAction();
            if (this$action == null ? other$action == null : this$action.equals(other$action)) {
               Object this$location = this.getLocation();
               Object other$location = other.getLocation();
               if (this$location == null ? other$location == null : this$location.equals(other$location)) {
                  Object this$direction = this.getDirection();
                  Object other$direction = other.getDirection();
                  return this$direction == null ? other$direction == null : this$direction.equals(other$direction);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof PlayerActionPacketEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isCancelled() ? 79 : 97);
      Object $player = this.getPlayer();
      result = result * 59 + ($player == null ? 43 : $player.hashCode());
      Object $action = this.getAction();
      result = result * 59 + ($action == null ? 43 : $action.hashCode());
      Object $location = this.getLocation();
      result = result * 59 + ($location == null ? 43 : $location.hashCode());
      Object $direction = this.getDirection();
      return result * 59 + ($direction == null ? 43 : $direction.hashCode());
   }

   public static enum Action {
      START_DESTROY_BLOCK,
      CHANGE_DESTROY_DIRECTION,
      ABORT_DESTROY_BLOCK,
      STOP_DESTROY_BLOCK,
      DROP_ALL_ITEMS,
      DROP_ITEM,
      RELEASE_USE_ITEM,
      SWAP_ITEM_WITH_OFFHAND,
      STAB;
   }
}
