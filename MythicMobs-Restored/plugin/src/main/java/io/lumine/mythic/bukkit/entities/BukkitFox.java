package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.TameableEntityType;
import io.lumine.mythic.bukkit.entities.properties.TameableProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Fox.Type;

@Getter
public class BukkitFox extends BukkitEntityType implements TameableEntityType {
   private AgeableProperty ageableProperty;
   private TameableProperty tameableProperty;
   private String foxType;

   @Override
   public EntityType getBukkitType() {
      return EntityType.FOX;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.foxType = mc.getString("Options.FoxType", null);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Fox e = (Fox)entity;
      this.ageableProperty.applyProperties(entity);
      if (this.foxType != null) {
         e.setFoxType(Type.valueOf(this.foxType));
      }

      return e;
   }
}
