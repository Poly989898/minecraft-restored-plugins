package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;

@Getter
public class BukkitPiglin extends BukkitEntityType {
   private boolean ableToHunt = true;
   private boolean immuneToZombification = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.PIGLIN;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ableToHunt = mc.getBoolean("Options.AbleToHunt", true);
      this.immuneToZombification = mc.getBoolean("Options.ImmuneToZombification", true);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Piglin z = (Piglin)entity;
      z.setBaby(false);
      z.setIsAbleToHunt(this.ableToHunt);
      z.setImmuneToZombification(this.immuneToZombification);
      return z;
   }

   @Override
   public boolean compare(Entity e) {
      return e instanceof Piglin piglin && !piglin.isBaby();
   }
}
