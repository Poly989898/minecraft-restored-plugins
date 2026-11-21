package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Goat;

@Getter
public class BukkitGoat extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   private boolean screaming;

   @Override
   public EntityType getBukkitType() {
      return EntityType.GOAT;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.screaming = mc.getBoolean("Options.Screaming", false);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Goat e = (Goat)entity;
      this.ageableProperty.applyProperties(entity);
      if (this.screaming) {
         e.setScreaming(true);
      }

      return e;
   }
}
