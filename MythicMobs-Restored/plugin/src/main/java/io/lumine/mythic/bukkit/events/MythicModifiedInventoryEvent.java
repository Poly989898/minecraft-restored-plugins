package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.skills.SkillCaster;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MythicModifiedInventoryEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final SkillCaster caster;
   private final Player player;

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @Generated
   public MythicModifiedInventoryEvent(SkillCaster caster, Player player) {
      this.caster = caster;
      this.player = player;
   }

   @Generated
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MythicModifiedInventoryEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$caster = this.getCaster();
         Object other$caster = other.getCaster();
         if (this$caster == null ? other$caster == null : this$caster.equals(other$caster)) {
            Object this$player = this.getPlayer();
            Object other$player = other.getPlayer();
            return this$player == null ? other$player == null : this$player.equals(other$player);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof MythicModifiedInventoryEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $caster = this.getCaster();
      result = result * 59 + ($caster == null ? 43 : $caster.hashCode());
      Object $player = this.getPlayer();
      return result * 59 + ($player == null ? 43 : $player.hashCode());
   }

   @Generated
   public String toString() {
      return "MythicModifiedInventoryEvent(caster=" + this.getCaster() + ", player=" + this.getPlayer() + ")";
   }
}
