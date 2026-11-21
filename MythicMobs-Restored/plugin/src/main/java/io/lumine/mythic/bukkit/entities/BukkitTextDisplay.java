package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.display.TextDisplayProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

@Getter
public class BukkitTextDisplay extends BukkitEntityType {
   private TextDisplayProperty textDisplayProperty;

   @Override
   public EntityType getBukkitType() {
      return EntityType.TEXT_DISPLAY;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.textDisplayProperty = new TextDisplayProperty(mc);
   }

   @Override
   public Entity applyOptions(Entity e) {
      return this.textDisplayProperty.applyProperties(e);
   }
}
