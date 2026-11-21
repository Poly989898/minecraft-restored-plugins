package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

@Getter
public class BukkitTNT extends BukkitEntityType {
   private int explosionFuseTicks = -1;
   private int explosionYield = -1;
   private boolean explosionIncendiary = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.TNT;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.explosionFuseTicks = mc.getInteger("Options.FuseTicks", -1);
      this.explosionYield = mc.getInteger("Options.ExplosionYield", -1);
      this.explosionIncendiary = mc.getBoolean("Options.Incendiary", false);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      TNTPrimed tnt = (TNTPrimed)entity;
      if (this.explosionFuseTicks > -1) {
         tnt.setFuseTicks(this.explosionFuseTicks);
      }

      if (this.explosionYield > -1) {
         tnt.setYield(this.explosionYield);
      }

      tnt.setIsIncendiary(this.explosionIncendiary);
      return tnt;
   }

   @Override
   public boolean compare(Entity e) {
      return e instanceof TNTPrimed;
   }
}
