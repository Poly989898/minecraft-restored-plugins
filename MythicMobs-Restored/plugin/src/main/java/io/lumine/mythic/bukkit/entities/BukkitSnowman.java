package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowman;

@Getter
public class BukkitSnowman extends BukkitEntityType {
   private Boolean preventSnowFormation = false;
   private boolean isDerp = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.SNOW_GOLEM;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.preventSnowFormation = mc.getBoolean("Options.PreventSnowFormation", false);
      this.isDerp = mc.getBoolean("Options.Derp", false);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Snowman e = (Snowman)entity;
      e.setDerp(this.isDerp);
      return e;
   }
}
