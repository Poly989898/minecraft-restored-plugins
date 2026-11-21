package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import org.bukkit.entity.EntityType;

public class BukkitHappyGhast extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.HAPPY_GHAST;
   }

   @Override
   public int getHeight() {
      return 5;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
   }
}
