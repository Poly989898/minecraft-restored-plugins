package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitVex extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.VEX;
   }

   @Override
   public int getHeight() {
      return 1;
   }
}
