package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.volatilecode.VolatileAttribute;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

@Getter
public class BukkitDrowned extends BukkitEntityType {
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
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      Drowned e = (Drowned)this.spawnEntity(location, EntityType.DROWNED, reason, prespawnFunc);
      e.setBaby(false);
      return e;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Drowned e = (Drowned)entity;
      if (this.reinforcementChance >= 0.0) {
         e.getAttribute(VolatileAttribute.SPAWN_REINFORCEMENTS).setBaseValue(this.reinforcementChance);
      }

      return e;
   }
}
