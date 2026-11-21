package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.display.ItemDisplayProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

@Getter
public class BukkitItemDisplay extends BukkitEntityType {
   private ItemDisplayProperty itemDisplayProperty;

   @Override
   public EntityType getBukkitType() {
      return EntityType.ITEM_DISPLAY;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.itemDisplayProperty = new ItemDisplayProperty(mc);
   }

   @Override
   public Entity applyOptions(Entity e) {
      return this.itemDisplayProperty.applyProperties(e);
   }
}
