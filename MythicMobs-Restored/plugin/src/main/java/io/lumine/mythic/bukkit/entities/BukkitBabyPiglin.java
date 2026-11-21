package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;

public class BukkitBabyPiglin extends BukkitEntityType {
   private boolean ableToHunt = true;
   private boolean immuneToZombification = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.PIGLIN;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ableToHunt = mc.getBoolean("Options.AbleToHunt", true);
      this.immuneToZombification = mc.getBoolean("Options.ImmuneToZombification", true);
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      Piglin e = (Piglin)this.spawnEntity(location, EntityType.PIGLIN, reason, prespawnFunc);
      e.setBaby(true);
      return e;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Piglin z = (Piglin)entity;
      z.setBaby(true);
      z.setIsAbleToHunt(this.ableToHunt);
      z.setImmuneToZombification(this.immuneToZombification);
      return z;
   }

   @Override
   public boolean compare(Entity e) {
      return e instanceof Piglin && ((Piglin)e).isBaby();
   }

   public static boolean isInstanceOf(Entity e) {
      return e instanceof Piglin ? ((Piglin)e).isBaby() : false;
   }
}
