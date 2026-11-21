package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.volatilecode.VolatileAttribute;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import lombok.Getter;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

@Getter
public class BukkitBabyDrowned extends BukkitEntityType {
   private double reinforcementChance = -1.0;

   @Override
   public EntityType getBukkitType() {
      return EntityType.DROWNED;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.reinforcementChance = mc.getDouble("Options.ReinforcementsChance", -1.0);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Drowned e = (Drowned)entity;
      e.setBaby();
      e.setAgeLock(true);
      if (this.reinforcementChance >= 0.0) {
         e.getAttribute(VolatileAttribute.SPAWN_REINFORCEMENTS).setBaseValue(this.reinforcementChance);
      }

      return e;
   }

   @Override
   public boolean compare(Entity e) {
      return isInstanceOf(e);
   }

   public static boolean isInstanceOf(Entity e) {
      return e instanceof Drowned && e.getType().equals(EntityType.DROWNED) ? ((Drowned)e).isBaby() : false;
   }
}
