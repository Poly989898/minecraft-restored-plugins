package io.lumine.mythic.bukkit.entities.properties;

import io.lumine.mythic.api.config.MythicConfig;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;

@Getter
public class TameableProperty implements EntityPropertySet {
   private boolean tameable = false;
   private boolean tamed = false;
   private final Set<Material> tamingMaterials;

   public TameableProperty(MythicConfig mc, Set<Material> tamingMaterials) {
      if (mc.isSet("Options.Tameable")) {
         this.tameable = mc.getBoolean("Options.Tameable", false);
      }

      if (mc.isSet("Options.Tamed")) {
         this.tamed = mc.getBoolean("Options.Tamed", false);
      }

      this.tamingMaterials = tamingMaterials;
   }

   @Override
   public Entity applyProperties(Entity entity) {
      Tameable tameable = (Tameable)entity;
      tameable.setTamed(this.tamed);
      return tameable;
   }
}
