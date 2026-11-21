package io.lumine.mythic.api.mobs.model;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class MobModel {
   private final MythicMob baseMob;
   private final MythicConfig config;

   public MobModel(MythicMob baseMob, MythicConfig config) {
      this.baseMob = baseMob;
      this.config = config;
   }

   public abstract void apply(AbstractEntity abstractEntity);

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MobModel other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$baseMob = this.getBaseMob();
         Object other$baseMob = other.getBaseMob();
         if (this$baseMob == null ? other$baseMob == null : this$baseMob.equals(other$baseMob)) {
            Object this$config = this.getConfig();
            Object other$config = other.getConfig();
            return this$config == null ? other$config == null : this$config.equals(other$config);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof MobModel;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $baseMob = this.getBaseMob();
      result = result * 59 + ($baseMob == null ? 43 : $baseMob.hashCode());
      Object $config = this.getConfig();
      return result * 59 + ($config == null ? 43 : $config.hashCode());
   }
}
