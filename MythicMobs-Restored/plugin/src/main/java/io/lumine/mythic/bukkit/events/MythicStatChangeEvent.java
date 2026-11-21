package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@ToString
public class MythicStatChangeEvent extends Event {
   private final SkillCaster caster;
   private final StatRegistry statRegistry;
   private final StatType stat;
   private final double oldValue;
   private final double newValue;
   private static final HandlerList handlers = new HandlerList();

   public MythicStatChangeEvent(SkillCaster caster, StatType stat, double oldValue, double newValue) {
      this(caster, null, stat, oldValue, newValue);
   }

   public MythicStatChangeEvent(SkillCaster caster, StatRegistry statRegistry, StatType stat, double oldValue, double newValue) {
      super(!Bukkit.isPrimaryThread());
      this.caster = caster;
      this.statRegistry = statRegistry;
      this.stat = stat;
      this.oldValue = oldValue;
      this.newValue = newValue;
   }

   public Entity getEntity() {
      return BukkitAdapter.adapt(this.caster.getEntity());
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
      } else if (!(o instanceof MythicStatChangeEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Double.compare(this.getOldValue(), other.getOldValue()) != 0) {
         return false;
      } else if (Double.compare(this.getNewValue(), other.getNewValue()) != 0) {
         return false;
      } else {
         Object this$caster = this.getCaster();
         Object other$caster = other.getCaster();
         if (this$caster == null ? other$caster == null : this$caster.equals(other$caster)) {
            Object this$statRegistry = this.getStatRegistry();
            Object other$statRegistry = other.getStatRegistry();
            if (this$statRegistry == null ? other$statRegistry == null : this$statRegistry.equals(other$statRegistry)) {
               Object this$stat = this.getStat();
               Object other$stat = other.getStat();
               return this$stat == null ? other$stat == null : this$stat.equals(other$stat);
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
      return other instanceof MythicStatChangeEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $oldValue = Double.doubleToLongBits(this.getOldValue());
      result = result * 59 + (int)($oldValue >>> 32 ^ $oldValue);
      long $newValue = Double.doubleToLongBits(this.getNewValue());
      result = result * 59 + (int)($newValue >>> 32 ^ $newValue);
      Object $caster = this.getCaster();
      result = result * 59 + ($caster == null ? 43 : $caster.hashCode());
      Object $statRegistry = this.getStatRegistry();
      result = result * 59 + ($statRegistry == null ? 43 : $statRegistry.hashCode());
      Object $stat = this.getStat();
      return result * 59 + ($stat == null ? 43 : $stat.hashCode());
   }
}
