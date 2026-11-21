package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Frog.Variant;

@Getter
public class BukkitFrog extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   private Variant variant;

   @Override
   public EntityType getBukkitType() {
      return EntityType.FROG;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      String type = mc.getNamespacedString("Options.Type", "warm");
      this.variant = Registry.FROG_VARIANT.get(NamespacedKey.fromString(type));
      if (this.variant == null) {
         MythicLogger.errorEntityConfig(this, mc, "Invalid FROG type provided");
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Frog e = (Frog)entity;
      e.setVariant(this.variant);
      this.ageableProperty.applyProperties(entity);
      return e;
   }
}
