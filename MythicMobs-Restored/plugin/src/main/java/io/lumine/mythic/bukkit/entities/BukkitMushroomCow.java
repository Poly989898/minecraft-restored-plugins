package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import lombok.Getter;
import org.bukkit.entity.EntityType;

@Getter
public class BukkitMushroomCow extends BukkitEntityType {
   private AgeableProperty ageableProperty;

   @Override
   public EntityType getBukkitType() {
      return EntityType.MOOSHROOM;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
   }
}
