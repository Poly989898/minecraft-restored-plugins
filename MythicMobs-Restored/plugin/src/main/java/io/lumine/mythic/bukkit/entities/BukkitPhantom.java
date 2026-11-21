package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;

@Getter
public class BukkitPhantom extends BukkitEntityType {
   private int size = -1;

   @Override
   public EntityType getBukkitType() {
      return EntityType.PHANTOM;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.size = mc.getInteger("Options.Size", -1);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Phantom e = (Phantom)entity;
      if (this.size > 0) {
         e.setSize(this.size);
      }

      return e;
   }
}
