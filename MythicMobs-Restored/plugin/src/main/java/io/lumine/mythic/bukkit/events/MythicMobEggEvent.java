package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class MythicMobEggEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Player player;
   private final MythicMob mobType;
   private final AbstractLocation location;
   private boolean cancelled = false;

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @Generated
   public MythicMobEggEvent(Player player, MythicMob mobType, AbstractLocation location) {
      this.player = player;
      this.mobType = mobType;
      this.location = location;
   }

   @Generated
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MythicMobEggEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isCancelled() != other.isCancelled()) {
         return false;
      } else {
         Object this$player = this.getPlayer();
         Object other$player = other.getPlayer();
         if (this$player == null ? other$player == null : this$player.equals(other$player)) {
            Object this$mobType = this.getMobType();
            Object other$mobType = other.getMobType();
            if (this$mobType == null ? other$mobType == null : this$mobType.equals(other$mobType)) {
               Object this$location = this.getLocation();
               Object other$location = other.getLocation();
               return this$location == null ? other$location == null : this$location.equals(other$location);
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
      return other instanceof MythicMobEggEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isCancelled() ? 79 : 97);
      Object $player = this.getPlayer();
      result = result * 59 + ($player == null ? 43 : $player.hashCode());
      Object $mobType = this.getMobType();
      result = result * 59 + ($mobType == null ? 43 : $mobType.hashCode());
      Object $location = this.getLocation();
      return result * 59 + ($location == null ? 43 : $location.hashCode());
   }

   @Generated
   public String toString() {
      return "MythicMobEggEvent(player="
         + this.getPlayer()
         + ", mobType="
         + this.getMobType()
         + ", location="
         + this.getLocation()
         + ", cancelled="
         + this.isCancelled()
         + ")";
   }
}
