package io.lumine.mythic.core.mobs.model;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.MPetCompat;

public class MPetsModel extends MobModel {
   private MPetCompat.MiniaturePetType mPetType = null;

   public MPetsModel(MythicMob baseMob, MythicConfig config) {
      super(baseMob, config);
      if (MythicBukkit.inst().getCompatibility().getMiniaturePets().isPresent()) {
         this.mPetType = MythicBukkit.inst().getCompatibility().getMiniaturePets().get().getModel(config);
      }
   }

   @Override
   public void apply(AbstractEntity entity) {
      if (this.mPetType != null && this.mPetType.isValid()) {
         this.mPetType.applyModel(entity);
      }
   }

   @Override
   public void remove(AbstractEntity entity) {
      if (this.mPetType != null && this.mPetType.isValid()) {
         this.mPetType.removeModel();
      }
   }
}
