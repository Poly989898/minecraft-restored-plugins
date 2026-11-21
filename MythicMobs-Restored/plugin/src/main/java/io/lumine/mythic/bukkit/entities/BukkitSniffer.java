package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import org.bukkit.entity.EntityType;

public class BukkitSniffer extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.SNIFFER;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
   }
}
