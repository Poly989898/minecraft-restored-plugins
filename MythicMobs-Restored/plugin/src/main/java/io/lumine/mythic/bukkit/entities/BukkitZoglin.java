package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zoglin;

public class BukkitZoglin extends BukkitEntityType {
   @Override
   public EntityType getBukkitType() {
      return EntityType.ZOGLIN;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Zoglin z = (Zoglin)entity;
      z.setBaby(false);
      return z;
   }

   @Override
   public boolean compare(Entity e) {
      return e instanceof Zoglin && !((Zoglin)e).isBaby();
   }
}
