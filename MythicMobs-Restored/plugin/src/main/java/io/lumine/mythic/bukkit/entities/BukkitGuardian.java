package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitGuardian extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.GUARDIAN;
   }

   @Override
   public int getHeight() {
      return 1;
   }
}
