package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
@ToString
public class MythicTriggerEvent extends Event implements Cancellable {
   private final IParentSkill parent;
   private final SkillTrigger trigger;
   private final SkillTriggerMetadata triggerMetadata;
   private final SkillMetadata skillMetadata;
   private static final HandlerList handlers = new HandlerList();
   private boolean canceled = false;

   public MythicTriggerEvent(IParentSkill parent, SkillTrigger trigger, SkillTriggerMetadata triggerMetadata, SkillMetadata skillMetadata, boolean sync) {
      super(!sync);
      this.parent = parent;
      this.trigger = trigger;
      this.triggerMetadata = triggerMetadata;
      this.skillMetadata = skillMetadata;
   }

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
      } else if (!(o instanceof MythicTriggerEvent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isCanceled() != other.isCanceled()) {
         return false;
      } else {
         Object this$parent = this.getParent();
         Object other$parent = other.getParent();
         if (this$parent == null ? other$parent == null : this$parent.equals(other$parent)) {
            Object this$trigger = this.getTrigger();
            Object other$trigger = other.getTrigger();
            if (this$trigger == null ? other$trigger == null : this$trigger.equals(other$trigger)) {
               Object this$triggerMetadata = this.getTriggerMetadata();
               Object other$triggerMetadata = other.getTriggerMetadata();
               if (this$triggerMetadata == null ? other$triggerMetadata == null : this$triggerMetadata.equals(other$triggerMetadata)) {
                  Object this$skillMetadata = this.getSkillMetadata();
                  Object other$skillMetadata = other.getSkillMetadata();
                  return this$skillMetadata == null ? other$skillMetadata == null : this$skillMetadata.equals(other$skillMetadata);
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
      return other instanceof MythicTriggerEvent;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isCanceled() ? 79 : 97);
      Object $parent = this.getParent();
      result = result * 59 + ($parent == null ? 43 : $parent.hashCode());
      Object $trigger = this.getTrigger();
      result = result * 59 + ($trigger == null ? 43 : $trigger.hashCode());
      Object $triggerMetadata = this.getTriggerMetadata();
      result = result * 59 + ($triggerMetadata == null ? 43 : $triggerMetadata.hashCode());
      Object $skillMetadata = this.getSkillMetadata();
      return result * 59 + ($skillMetadata == null ? 43 : $skillMetadata.hashCode());
   }
}
