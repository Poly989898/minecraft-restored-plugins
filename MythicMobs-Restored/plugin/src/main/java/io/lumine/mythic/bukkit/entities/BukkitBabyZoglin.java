package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zoglin;

public class BukkitBabyZoglin extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.ZOGLIN;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      Zoglin e = (Zoglin)this.spawnEntity(location, EntityType.ZOGLIN, reason, prespawnFunc);
      e.setBaby(true);
      return e;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Zoglin z = (Zoglin)entity;
      z.setBaby(true);
      return z;
   }

   @Override
   public boolean compare(Entity e) {
      return e instanceof Zoglin && ((Zoglin)e).isBaby();
   }
}
