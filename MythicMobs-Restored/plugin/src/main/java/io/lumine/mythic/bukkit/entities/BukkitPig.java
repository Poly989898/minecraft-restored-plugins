package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Pig.Variant;

@Getter
public class BukkitPig extends BukkitEntityType {
   private boolean saddled = false;
   private NamespacedKey variant;

   @Override
   public EntityType getBukkitType() {
      return EntityType.PIG;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
      this.saddled = mc.getBoolean("Options.Saddled", false);
      String variant = mc.getNamespacedString(new String[]{"Variant", "Options.Variant"}, null);
      if (variant != null) {
         this.variant = NamespacedKey.fromString(variant);
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Pig e = (Pig)entity;
      e.setSaddle(this.saddled);
      if (this.variant != null) {
         Variant variant = RegistryAccess.registryAccess().getRegistry(RegistryKey.PIG_VARIANT).get(this.variant);
         if (variant != null) {
            e.setVariant(variant);
         }
      }

      return e;
   }
}
