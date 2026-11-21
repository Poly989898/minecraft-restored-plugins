package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BukkitSulfurCube extends BukkitEntityType {
   private String archetype;

   @Override
   public EntityType getBukkitType() {
      return EntityType.valueOf("SULFUR_CUBE");
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
      this.archetype = mc.getString("Options.Archetype", null);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      VolatileCompatibilityHandler compatibility = this.getPlugin().getVolatileCodeHandler().getCompatibilityHandler();
      if (!((Ageable)entity).isAdult()) {
         compatibility.setCubeMobSize(entity, 1);
      }

      if (this.archetype != null) {
         compatibility.setSulfurCubeArchetype(entity, this.archetype);
      }

      return entity;
   }
}
