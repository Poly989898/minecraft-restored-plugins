package io.lumine.mythic.api.skills.heal;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import java.util.Collection;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HealMetadata {
   private final SkillCaster healer;
   private final SkillMetadata metadata;
   private final AbstractItemStack healerItem;
   private final AbstractEntity target;
   private double amount;
   private double multiplier = 1.0;
   private final Collection<String> tags = Sets.newHashSet();

   public HealMetadata(SkillCaster healer, SkillMetadata metadata, AbstractItemStack healerItem, AbstractEntity target, double amount, double multiplier) {
      this.healer = healer;
      this.metadata = metadata;
      this.healerItem = healerItem;
      this.target = target;
      this.amount = amount;
      this.multiplier = multiplier;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof HealMetadata other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Double.compare(this.getAmount(), other.getAmount()) != 0) {
         return false;
      } else if (Double.compare(this.getMultiplier(), other.getMultiplier()) != 0) {
         return false;
      } else {
         Object this$healer = this.getHealer();
         Object other$healer = other.getHealer();
         if (this$healer == null ? other$healer == null : this$healer.equals(other$healer)) {
            Object this$metadata = this.getMetadata();
            Object other$metadata = other.getMetadata();
            if (this$metadata == null ? other$metadata == null : this$metadata.equals(other$metadata)) {
               Object this$healerItem = this.getHealerItem();
               Object other$healerItem = other.getHealerItem();
               if (this$healerItem == null ? other$healerItem == null : this$healerItem.equals(other$healerItem)) {
                  Object this$target = this.getTarget();
                  Object other$target = other.getTarget();
                  if (this$target == null ? other$target == null : this$target.equals(other$target)) {
                     Object this$tags = this.getTags();
                     Object other$tags = other.getTags();
                     return this$tags == null ? other$tags == null : this$tags.equals(other$tags);
                  } else {
                     return false;
                  }
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
      return other instanceof HealMetadata;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $amount = Double.doubleToLongBits(this.getAmount());
      result = result * 59 + (int)($amount >>> 32 ^ $amount);
      long $multiplier = Double.doubleToLongBits(this.getMultiplier());
      result = result * 59 + (int)($multiplier >>> 32 ^ $multiplier);
      Object $healer = this.getHealer();
      result = result * 59 + ($healer == null ? 43 : $healer.hashCode());
      Object $metadata = this.getMetadata();
      result = result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
      Object $healerItem = this.getHealerItem();
      result = result * 59 + ($healerItem == null ? 43 : $healerItem.hashCode());
      Object $target = this.getTarget();
      result = result * 59 + ($target == null ? 43 : $target.hashCode());
      Object $tags = this.getTags();
      return result * 59 + ($tags == null ? 43 : $tags.hashCode());
   }
}
