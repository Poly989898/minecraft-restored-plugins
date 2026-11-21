package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.variables.Variable;
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
public class MythicPlayerVariableRemoveEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final PlayerData profile;
   private final String key;
   private final Variable variable;
   private boolean cancelled = false;

   public MythicPlayerVariableRemoveEvent(PlayerData profile, String key, Variable variable) {
      super(!Bukkit.getServer().isPrimaryThread());
      this.profile = profile;
      this.key = key;
      this.variable = variable;
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
      } else if (!(o instanceof MythicPlayerVariableRemoveEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isCancelled() != other.isCancelled()) {
         return false;
      } else {
         Object this$profile = this.getProfile();
         Object other$profile = other.getProfile();
         if (this$profile == null ? other$profile == null : this$profile.equals(other$profile)) {
            Object this$key = this.getKey();
            Object other$key = other.getKey();
            if (this$key == null ? other$key == null : this$key.equals(other$key)) {
               Object this$variable = this.getVariable();
               Object other$variable = other.getVariable();
               return this$variable == null ? other$variable == null : this$variable.equals(other$variable);
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
      return other instanceof MythicPlayerVariableRemoveEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isCancelled() ? 79 : 97);
      Object $profile = this.getProfile();
      result = result * 59 + ($profile == null ? 43 : $profile.hashCode());
      Object $key = this.getKey();
      result = result * 59 + ($key == null ? 43 : $key.hashCode());
      Object $variable = this.getVariable();
      return result * 59 + ($variable == null ? 43 : $variable.hashCode());
   }
}
