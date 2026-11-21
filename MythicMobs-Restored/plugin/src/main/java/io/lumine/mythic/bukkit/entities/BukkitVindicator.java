package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.RaiderProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

@Getter
public class BukkitVindicator extends BukkitEntityType {
   private RaiderProperty raiderProperty;

   @Override
   public EntityType getBukkitType() {
      return EntityType.VINDICATOR;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.raiderProperty = new RaiderProperty(mc);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      this.raiderProperty.applyProperties(entity);
      return entity;
   }
}
