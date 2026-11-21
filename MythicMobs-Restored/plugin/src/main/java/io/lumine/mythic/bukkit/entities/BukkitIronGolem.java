package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

@Getter
public class BukkitIronGolem extends BukkitEntityType {
   private boolean isPlayerCreated = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.IRON_GOLEM;
   }

   @Override
   public int getHeight() {
      return 3;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.isPlayerCreated = mc.getBoolean("Options.PlayerCreated", false);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      IronGolem e = (IronGolem)entity;
      e.setPlayerCreated(this.isPlayerCreated);
      return e;
   }
}
