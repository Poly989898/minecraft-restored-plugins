package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

@Getter
public class BukkitSheep extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   protected DyeColor color;
   private boolean isSheared = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.SHEEP;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      String strcolor = mc.getString("Options.Color", "WHITE");
      this.color = DyeColor.valueOf(strcolor);
      this.isSheared = mc.getBoolean("Options.Sheared", false);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Sheep e = (Sheep)entity;
      this.ageableProperty.applyProperties(entity);
      e.setColor(this.color);
      e.setSheared(this.isSheared);
      return e;
   }
}
