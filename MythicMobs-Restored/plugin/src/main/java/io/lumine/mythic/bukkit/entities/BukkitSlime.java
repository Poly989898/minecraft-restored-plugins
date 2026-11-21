package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.mobs.PersistentMobData;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.persistence.PersistentDataType;

@Getter
public class BukkitSlime extends BukkitEntityType {
   private int size = -1;
   private boolean preventSplit = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.SLIME;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.size = mc.getInt("Options.Size", -1);
      this.preventSplit = mc.getBoolean("Options.PreventSplit", false);
      this.preventSplit = mc.getBoolean("Options.PreventSplitting", this.preventSplit);
      this.preventSplit = mc.getBoolean("Options.PreventSlimeSplit", this.preventSplit);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Slime e = (Slime)entity;
      if (this.size > 0) {
         e.setSize(this.size);
      }

      if (this.preventSplit) {
         e.getPersistentDataContainer().set(PersistentMobData.SLIME_NO_SPLIT, PersistentDataType.BOOLEAN, true);
      }

      return e;
   }

   public boolean canSplit() {
      return !this.preventSplit;
   }
}
