package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitTadpole extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.TADPOLE;
   }

   @Override
   public int getHeight() {
      return 1;
   }
}
