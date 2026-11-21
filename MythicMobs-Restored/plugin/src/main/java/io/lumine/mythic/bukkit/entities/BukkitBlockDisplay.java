package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.display.BlockDisplayProperty;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BukkitBlockDisplay extends BukkitEntityType {
   private BlockDisplayProperty blockDisplayProperty;

   @Override
   public EntityType getBukkitType() {
      return EntityType.BLOCK_DISPLAY;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.blockDisplayProperty = new BlockDisplayProperty(mc);
   }

   @Override
   public Entity applyOptions(Entity e) {
      return this.blockDisplayProperty.applyProperties(e);
   }
}
