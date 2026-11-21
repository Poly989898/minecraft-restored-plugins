package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Chicken.Variant;

public class BukkitChicken extends BukkitEntityType {
   private boolean jockey = false;
   private NamespacedKey variant;

   @Override
   public EntityType getBukkitType() {
      return EntityType.CHICKEN;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
      this.jockey = mc.getBoolean("Options.Jockey", false);
      String variant = mc.getNamespacedString(new String[]{"Variant", "Options.Variant"}, null);
      if (variant != null) {
         this.variant = NamespacedKey.fromString(variant);
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Chicken e = (Chicken)entity;
      if (this.jockey) {
         e.setIsChickenJockey(true);
      }

      if (this.variant != null) {
         Variant variant = RegistryAccess.registryAccess().getRegistry(RegistryKey.CHICKEN_VARIANT).get(this.variant);
         if (variant != null) {
            e.setVariant(variant);
         }
      }

      return e;
   }
}
