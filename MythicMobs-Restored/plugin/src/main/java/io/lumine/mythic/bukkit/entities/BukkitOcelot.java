package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;

@Getter
public class BukkitOcelot extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   private boolean tameable;
   private String ocelotType;

   @Override
   public EntityType getBukkitType() {
      return EntityType.OCELOT;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.ocelotType = mc.getString("Options.Ocelot", null);
      this.ocelotType = mc.getString("Options.OcelotType", this.ocelotType);
      this.tameable = mc.getBoolean("Options.Tameable", false);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Ocelot e = (Ocelot)entity;
      this.ageableProperty.applyProperties(entity);
      if (this.ocelotType != null) {
         if (ServerVersion.isBefore(MinecraftVersions.v1_14)) {
            e.setCatType(Type.valueOf(this.ocelotType));
         } else {
            MythicLogger.errorGenericConfig("Ocelots no longer have cat types in versions >= 1.14");
         }
      }

      return e;
   }
}
