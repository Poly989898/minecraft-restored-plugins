package io.lumine.mythic.core.spawning.random;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.utils.collections.AWeightedItem;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RandomSpawnMob extends AWeightedItem {
   private final MythicMob mob;

   public RandomSpawnMob(MythicMob mob, double weight) {
      super(weight);
      this.mob = mob;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof RandomSpawnMob other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$mob = this.getMob();
         Object other$mob = other.getMob();
         return this$mob == null ? other$mob == null : this$mob.equals(other$mob);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof RandomSpawnMob;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $mob = this.getMob();
      return result * 59 + ($mob == null ? 43 : $mob.hashCode());
   }
}
