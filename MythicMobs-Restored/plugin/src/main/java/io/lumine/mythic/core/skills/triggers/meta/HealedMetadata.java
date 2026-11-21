package io.lumine.mythic.core.skills.triggers.meta;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.entity.EntityRegainHealthEvent;

@Getter
@Setter
@ToString
public class HealedMetadata extends SkillTriggerMetadata implements CancellableTriggerMetadata {
   private final EntityRegainHealthEvent event;
   private final AbstractEntity healed;
   private double amount;
   private String regainReason;

   public HealedMetadata(EntityRegainHealthEvent event, AbstractEntity healed) {
      this.event = event;
      this.healed = healed;
      this.amount = event.getAmount();
      this.regainReason = event.getRegainReason().toString();
   }

   @Override
   public boolean isCancelled() {
      return this.event.isCancelled();
   }

   @Override
   public void applyToSkillMetadata(SkillMetadata skillMetadata) {
      skillMetadata.getVariables().putDouble("heal-amount", this.amount);
      skillMetadata.getVariables().putString("heal-cause", this.regainReason);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof HealedMetadata other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Double.compare(this.getAmount(), other.getAmount()) != 0) {
         return false;
      } else {
         Object this$event = this.getEvent();
         Object other$event = other.getEvent();
         if (this$event == null ? other$event == null : this$event.equals(other$event)) {
            Object this$healed = this.getHealed();
            Object other$healed = other.getHealed();
            if (this$healed == null ? other$healed == null : this$healed.equals(other$healed)) {
               Object this$regainReason = this.getRegainReason();
               Object other$regainReason = other.getRegainReason();
               return this$regainReason == null ? other$regainReason == null : this$regainReason.equals(other$regainReason);
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
      return other instanceof HealedMetadata;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $amount = Double.doubleToLongBits(this.getAmount());
      result = result * 59 + (int)($amount >>> 32 ^ $amount);
      Object $event = this.getEvent();
      result = result * 59 + ($event == null ? 43 : $event.hashCode());
      Object $healed = this.getHealed();
      result = result * 59 + ($healed == null ? 43 : $healed.hashCode());
      Object $regainReason = this.getRegainReason();
      return result * 59 + ($regainReason == null ? 43 : $regainReason.hashCode());
   }
}
