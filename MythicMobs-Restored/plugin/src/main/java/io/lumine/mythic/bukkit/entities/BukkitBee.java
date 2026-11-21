package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import lombok.Getter;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BukkitBee extends BukkitEntityType {
   private int anger = 0;
   private boolean hasNectar = false;
   private boolean hasStung = false;
   @Getter
   private boolean preventStingerLoss = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.BEE;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
      this.anger = mc.getInteger("Options.Anger", 0);
      this.hasNectar = mc.getBoolean("Options.HasNectar", false);
      this.hasStung = mc.getBoolean("Options.HasStung", false);
      this.preventStingerLoss = mc.getBoolean("Options.PreventStingerLoss", false);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Bee bee = (Bee)entity;
      bee.setAnger(this.anger);
      bee.setHasNectar(this.hasNectar);
      bee.setHasStung(this.hasStung);
      return bee;
   }
}
