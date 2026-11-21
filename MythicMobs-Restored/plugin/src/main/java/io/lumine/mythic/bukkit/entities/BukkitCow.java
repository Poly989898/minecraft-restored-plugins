package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BukkitCow extends BukkitEntityType {
   private String variant;

   @Override
   public EntityType getBukkitType() {
      return EntityType.COW;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
      this.variant = mc.getString(new String[]{"Variant", "Options.Variant"}, null);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Cow e = (Cow)entity;
      if (this.variant != null) {
         this.getPlugin().getVolatileCodeHandler().getCompatibilityHandler().setCowVariant(e, this.variant);
      }

      return e;
   }
}
