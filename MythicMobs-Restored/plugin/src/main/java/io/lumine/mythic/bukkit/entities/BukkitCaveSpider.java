package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitCaveSpider extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.CAVE_SPIDER;
   }

   @Override
   public int getHeight() {
      return 1;
   }
}
