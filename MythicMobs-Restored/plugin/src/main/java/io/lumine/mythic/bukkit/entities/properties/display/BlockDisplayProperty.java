package io.lumine.mythic.bukkit.entities.properties.display;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;

public class BlockDisplayProperty extends DisplayProperty {
   private AbstractBlock block;

   public BlockDisplayProperty(MythicConfig config) {
      super(config);
      String block = config.getString("DisplayOptions.Block", "grass_block");
      this.block = MythicBukkit.inst().getBootstrap().getBlock(block);
   }

   @Override
   public Entity applyProperties(Entity entity) {
      entity = super.applyProperties(entity);
      if (entity instanceof BlockDisplay blockDisplay && this.block.getBlockData() != null) {
         blockDisplay.setBlock(this.block.getBlockData());
      }

      return entity;
   }
}
