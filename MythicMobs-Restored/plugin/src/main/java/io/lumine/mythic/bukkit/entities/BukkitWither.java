package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitWither extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.WITHER;
   }

   @Override
   public int getHeight() {
      return 3;
   }
}
