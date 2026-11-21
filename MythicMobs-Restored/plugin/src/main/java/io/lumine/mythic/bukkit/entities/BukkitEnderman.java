package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

@Getter
public class BukkitEnderman extends BukkitEntityType {
   private EntityType bukkitType = EntityType.ENDERMAN;
   private static final int HEIGHT = 1;
   private Material heldBlock = Material.AIR;

   @Override
   public void instantiate(MythicConfig mc) {
      try {
         this.heldBlock = Material.valueOf(mc.getString("Options.HeldBlock", "AIR"));
      } catch (Error | Exception e) {
         MythicLogger.errorEntityConfig(this, mc, "Invalid Material specified for Options.HeldBlock");
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Enderman e = (Enderman)entity;
      if (this.heldBlock != Material.AIR) {
         e.setCarriedBlock(this.heldBlock.createBlockData());
      }

      return entity;
   }
}
