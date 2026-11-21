package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitSquid extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.SQUID;
   }

   @Override
   public int getHeight() {
      return 1;
   }
}
