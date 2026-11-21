package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hoglin;

@Getter
public class BukkitHoglin extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   private boolean ableToBeHunted = true;
   private boolean immuneToZombification = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.HOGLIN;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.ableToBeHunted = mc.getBoolean("Options.AbleToHunted", true);
      this.ableToBeHunted = mc.getBoolean("Options.Huntable", this.ableToBeHunted);
      this.immuneToZombification = mc.getBoolean("Options.ImmuneToZombification", false);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Hoglin hoglin = (Hoglin)entity;
      this.ageableProperty.applyProperties(hoglin);
      hoglin.setImmuneToZombification(this.immuneToZombification);
      hoglin.setIsAbleToBeHunted(this.ableToBeHunted);
      return hoglin;
   }
}
