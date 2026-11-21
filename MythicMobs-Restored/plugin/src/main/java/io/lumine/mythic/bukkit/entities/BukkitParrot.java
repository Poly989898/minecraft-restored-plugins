package io.lumine.mythic.bukkit.entities;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.TameableEntityType;
import io.lumine.mythic.bukkit.entities.properties.TameableProperty;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Parrot.Variant;

public class BukkitParrot extends BukkitEntityType implements TameableEntityType {
   private TameableProperty tameableProperty;
   private Variant variant;

   @Override
   public EntityType getBukkitType() {
      return EntityType.PARROT;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
      this.tameableProperty = new TameableProperty(
         mc, Sets.immutableEnumSet(Material.BEETROOT_SEEDS, new Material[]{Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Material.WHEAT_SEEDS})
      );
      String strcolor = mc.getString("Options.Color", null);
      strcolor = mc.getString("Options.Variant", strcolor);
      if (strcolor != null) {
         try {
            this.variant = Variant.valueOf(strcolor);
         } catch (Exception e) {
            MythicLogger.errorGenericConfig("Invalid parrot color/variant specified");
         }
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Parrot e = (Parrot)entity;
      if (this.variant != null) {
         e.setVariant(this.variant);
      }

      return e;
   }

   @Generated
   public Variant getVariant() {
      return this.variant;
   }

   @Generated
   @Override
   public TameableProperty getTameableProperty() {
      return this.tameableProperty;
   }
}
