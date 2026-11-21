package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitEndermite extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.ENDERMITE;
   }

   @Override
   public int getHeight() {
      return 1;
   }
}
