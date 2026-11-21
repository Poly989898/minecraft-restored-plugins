package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitParched extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.PARCHED;
   }

   @Override
   public int getHeight() {
      return 2;
   }
}
