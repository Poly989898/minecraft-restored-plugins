package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitCreaking extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.CREAKING;
   }

   @Override
   public int getHeight() {
      return 3;
   }
}
