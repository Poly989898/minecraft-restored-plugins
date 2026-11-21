package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitGlowSquid extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.GLOW_SQUID;
   }

   @Override
   public int getHeight() {
      return 2;
   }
}
