package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PolarBear;

@Getter
public class BukkitPolarBear extends BukkitEntityType {
   private AgeableProperty ageableProperty;

   @Override
   public EntityType getBukkitType() {
      return EntityType.POLAR_BEAR;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      PolarBear e = (PolarBear)entity;
      this.ageableProperty.applyProperties(entity);
      return e;
   }
}
