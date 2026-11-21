package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.core.players.PlayerData;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString
public class MythicPlayerSignalEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final PlayerData profile;
   private final String signal;
   private Boolean wasHandled = false;

   public MythicPlayerSignalEvent(PlayerData profile, String signal) {
      super(!Bukkit.getServer().isPrimaryThread());
      this.profile = profile;
      this.signal = signal;
   }

   @NotNull
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
      } else if (!(o instanceof MythicPlayerSignalEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$wasHandled = this.getWasHandled();
         Object other$wasHandled = other.getWasHandled();
         if (this$wasHandled == null ? other$wasHandled == null : this$wasHandled.equals(other$wasHandled)) {
            Object this$profile = this.getProfile();
            Object other$profile = other.getProfile();
            if (this$profile == null ? other$profile == null : this$profile.equals(other$profile)) {
               Object this$signal = this.getSignal();
               Object other$signal = other.getSignal();
               return this$signal == null ? other$signal == null : this$signal.equals(other$signal);
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
      return other instanceof MythicPlayerSignalEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $wasHandled = this.getWasHandled();
      result = result * 59 + ($wasHandled == null ? 43 : $wasHandled.hashCode());
      Object $profile = this.getProfile();
      result = result * 59 + ($profile == null ? 43 : $profile.hashCode());
      Object $signal = this.getSignal();
      return result * 59 + ($signal == null ? 43 : $signal.hashCode());
   }
}
