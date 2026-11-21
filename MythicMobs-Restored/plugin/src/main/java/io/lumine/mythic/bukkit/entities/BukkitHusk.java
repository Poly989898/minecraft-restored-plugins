package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.volatilecode.VolatileAttribute;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;

@Getter
public class BukkitHusk extends BukkitEntityType {
   private double reinforcementChance = -1.0;
   private boolean preventConversion = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.HUSK;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.reinforcementChance = mc.getDouble("Options.ReinforcementsChance", -1.0);
      this.preventConversion = mc.getBoolean("Options.PreventConversion", MythicBukkit.inst().getConfiguration().getPreventConversionByDefault());
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Husk e = (Husk)entity;
      e.setBaby(false);
      e.getAttribute(VolatileAttribute.SPAWN_REINFORCEMENTS).setBaseValue(this.reinforcementChance);
      if (this.preventConversion) {
         e.setConversionTime(Integer.MAX_VALUE);
      }

      return e;
   }
}
