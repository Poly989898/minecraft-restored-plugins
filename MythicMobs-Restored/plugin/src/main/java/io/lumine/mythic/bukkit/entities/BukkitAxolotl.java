package io.lumine.mythic.bukkit.entities;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.TameableEntityType;
import io.lumine.mythic.bukkit.entities.properties.TameableProperty;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Locale;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Axolotl.Variant;
import org.bukkit.inventory.ItemStack;

public class BukkitAxolotl extends BukkitEntityType implements TameableEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.AXOLOTL_BUCKET).build();
   @Getter
   private TameableProperty tameableProperty;
   @Getter
   private boolean tameable;
   private Variant variant;

   @Override
   public EntityType getBukkitType() {
      return EntityType.AXOLOTL;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
      this.tameableProperty = new TameableProperty(mc, Sets.immutableEnumSet(Material.WATER_BUCKET, new Material[]{Material.TROPICAL_FISH_BUCKET}));
      this.tameable = mc.getBoolean("Options.Tameable", false);
      String variant = mc.getString(new String[]{"Options.Variant", "Options.AxolotlType", "Options.Type"}, null);
      if (variant != null) {
         try {
            this.variant = Variant.valueOf(normalizeVariantName(variant));
         } catch (IllegalArgumentException e) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid axolotl variant configured");
         }
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Axolotl e = (Axolotl)entity;
      if (this.variant != null) {
         e.setVariant(this.variant);
      }

      return e;
   }

   private static String normalizeVariantName(String value) {
      String normalized = value.trim();
      int namespaceSplit = normalized.indexOf(58);
      if (namespaceSplit >= 0) {
         normalized = normalized.substring(namespaceSplit + 1);
      }

      return normalized.replace('-', '_').toUpperCase(Locale.ROOT);
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
