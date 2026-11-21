package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
@ToString
public class MythicSkillEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final SkillMetadata skillMetadata;
   private final Skill skill;
   private boolean cancelled = false;

   public MythicSkillEvent(SkillMetadata skillMetadata, Skill skill) {
      super(!Bukkit.isPrimaryThread());
      this.skillMetadata = skillMetadata;
      this.skill = skill;
   }

   @Deprecated
   public void setCancelled() {
      this.cancelled = true;
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
      } else if (!(o instanceof MythicSkillEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isCancelled() != other.isCancelled()) {
         return false;
      } else {
         Object this$skillMetadata = this.getSkillMetadata();
         Object other$skillMetadata = other.getSkillMetadata();
         if (this$skillMetadata == null ? other$skillMetadata == null : this$skillMetadata.equals(other$skillMetadata)) {
            Object this$skill = this.getSkill();
            Object other$skill = other.getSkill();
            return this$skill == null ? other$skill == null : this$skill.equals(other$skill);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof MythicSkillEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isCancelled() ? 79 : 97);
      Object $skillMetadata = this.getSkillMetadata();
      result = result * 59 + ($skillMetadata == null ? 43 : $skillMetadata.hashCode());
      Object $skill = this.getSkill();
      return result * 59 + ($skill == null ? 43 : $skill.hashCode());
   }
}
