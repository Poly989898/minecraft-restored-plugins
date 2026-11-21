package io.lumine.mythic.bukkit.utils.storage.events;

import io.lumine.mythic.bukkit.utils.storage.players.Profile;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LumineProfileSaveEvent<P extends Profile> extends Event {
   private final UUID uniqueId;
   private final P profile;
   private static final HandlerList handlers = new HandlerList();

   public LumineProfileSaveEvent(UUID uuid, P profile) {
      super(!Bukkit.isPrimaryThread());
      this.uniqueId = uuid;
      this.profile = profile;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public P getProfile() {
      return this.profile;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof LumineProfileSaveEvent<?> other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$uniqueId = this.getUniqueId();
         Object other$uniqueId = other.getUniqueId();
         if (this$uniqueId == null ? other$uniqueId == null : this$uniqueId.equals(other$uniqueId)) {
            Object this$profile = this.getProfile();
            Object other$profile = other.getProfile();
            return this$profile == null ? other$profile == null : this$profile.equals(other$profile);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof LumineProfileSaveEvent;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $uniqueId = this.getUniqueId();
      result = result * 59 + ($uniqueId == null ? 43 : $uniqueId.hashCode());
      Object $profile = this.getProfile();
      return result * 59 + ($profile == null ? 43 : $profile.hashCode());
   }

   public String toString() {
      return "LumineProfileSaveEvent(uniqueId=" + this.getUniqueId() + ", profile=" + this.getProfile() + ")";
   }
}
