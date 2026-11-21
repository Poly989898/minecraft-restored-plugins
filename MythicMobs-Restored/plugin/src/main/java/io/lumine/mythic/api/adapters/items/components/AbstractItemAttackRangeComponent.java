package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractItemAttackRangeComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "AttackRange";
   public static final String KEY_MIN = "AttackRange.Min";
   public static final String KEY_MAX = "AttackRange.Max";
   public static final String KEY_MIN_CREATIVE = "AttackRange.MinCreative";
   public static final String KEY_MAX_CREATIVE = "AttackRange.MaxCreative";
   public static final String KEY_HITBOX_MARGIN = "AttackRange.HitboxMargin";
   public static final String KEY_MOB_FACTOR = "AttackRange.MobFactor";
   private Float minReach;
   private Float maxReach;
   private Float minCreativeReach;
   private Float maxCreativeReach;
   private Float hitboxMargin;
   private Float mobFactor;

   public AbstractItemAttackRangeComponent(MythicItem mythicItem, float maxReach) {
      this.maxReach = maxReach;
   }

   public AbstractItemAttackRangeComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("AttackRange.Min")) {
         this.minReach = config.getFloat("AttackRange.Min", 0.0F);
      }

      if (config.isSet("AttackRange.Max")) {
         this.maxReach = config.getFloat("AttackRange.Max", 3.0F);
      }

      if (config.isSet("AttackRange.MinCreative")) {
         this.minCreativeReach = config.getFloat("AttackRange.MinCreative", 0.0F);
      }

      if (config.isSet("AttackRange.MaxCreative")) {
         this.maxCreativeReach = config.getFloat("AttackRange.MaxCreative", 5.0F);
      }

      if (config.isSet("AttackRange.HitboxMargin")) {
         this.hitboxMargin = config.getFloat("AttackRange.HitboxMargin", 0.3F);
      }

      if (config.isSet("AttackRange.MobFactor")) {
         this.mobFactor = config.getFloat("AttackRange.MobFactor", 1.0F);
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyAttackRangeComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemAttackRangeComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$minReach = this.getMinReach();
         Object other$minReach = other.getMinReach();
         if (this$minReach == null ? other$minReach == null : this$minReach.equals(other$minReach)) {
            Object this$maxReach = this.getMaxReach();
            Object other$maxReach = other.getMaxReach();
            if (this$maxReach == null ? other$maxReach == null : this$maxReach.equals(other$maxReach)) {
               Object this$minCreativeReach = this.getMinCreativeReach();
               Object other$minCreativeReach = other.getMinCreativeReach();
               if (this$minCreativeReach == null ? other$minCreativeReach == null : this$minCreativeReach.equals(other$minCreativeReach)) {
                  Object this$maxCreativeReach = this.getMaxCreativeReach();
                  Object other$maxCreativeReach = other.getMaxCreativeReach();
                  if (this$maxCreativeReach == null ? other$maxCreativeReach == null : this$maxCreativeReach.equals(other$maxCreativeReach)) {
                     Object this$hitboxMargin = this.getHitboxMargin();
                     Object other$hitboxMargin = other.getHitboxMargin();
                     if (this$hitboxMargin == null ? other$hitboxMargin == null : this$hitboxMargin.equals(other$hitboxMargin)) {
                        Object this$mobFactor = this.getMobFactor();
                        Object other$mobFactor = other.getMobFactor();
                        return this$mobFactor == null ? other$mobFactor == null : this$mobFactor.equals(other$mobFactor);
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
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemAttackRangeComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $minReach = this.getMinReach();
      result = result * 59 + ($minReach == null ? 43 : $minReach.hashCode());
      Object $maxReach = this.getMaxReach();
      result = result * 59 + ($maxReach == null ? 43 : $maxReach.hashCode());
      Object $minCreativeReach = this.getMinCreativeReach();
      result = result * 59 + ($minCreativeReach == null ? 43 : $minCreativeReach.hashCode());
      Object $maxCreativeReach = this.getMaxCreativeReach();
      result = result * 59 + ($maxCreativeReach == null ? 43 : $maxCreativeReach.hashCode());
      Object $hitboxMargin = this.getHitboxMargin();
      result = result * 59 + ($hitboxMargin == null ? 43 : $hitboxMargin.hashCode());
      Object $mobFactor = this.getMobFactor();
      return result * 59 + ($mobFactor == null ? 43 : $mobFactor.hashCode());
   }
}
