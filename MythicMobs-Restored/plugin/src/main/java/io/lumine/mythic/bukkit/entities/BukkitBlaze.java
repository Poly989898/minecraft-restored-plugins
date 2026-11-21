package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitBlaze extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.BLAZE;
   }

   @Override
   public int getHeight() {
      return 2;
   }
}
