package io.lumine.mythic.bukkit.entities;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.TameableEntityType;
import io.lumine.mythic.bukkit.entities.properties.TameableProperty;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitCat extends BukkitEntityType implements TameableEntityType {
   private final String headString = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJjMWU4MWZmMDNlODJhM2U3MWUwY2Q1ZmJlYzYwN2UxMTM2MTA4OWFhNDdmMjkwZDQ2YzhhMmMwNzQ2MGQ5MiJ9fX0";
   @Getter
   private final ItemStack head = BukkitAdapter.adapt(
      MythicBukkit.inst()
         .getBootstrap()
         .createItem("PLAYER_HEAD")
         .skullTexture(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJjMWU4MWZmMDNlODJhM2U3MWUwY2Q1ZmJlYzYwN2UxMTM2MTA4OWFhNDdmMjkwZDQ2YzhhMmMwNzQ2MGQ5MiJ9fX0"
         )
   );
   @Getter
   private TameableProperty tameableProperty;
   @Getter
   private boolean tameable = false;
   private boolean tamed = false;
   private String variant;
   private DyeColor collarColor;

   @Override
   public EntityType getBukkitType() {
      return EntityType.CAT;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.registerProperty(new AgeableProperty(mc));
      this.tameableProperty = new TameableProperty(mc, Sets.immutableEnumSet(Material.COD, new Material[]{Material.SALMON}));
      this.tameable = mc.getBoolean("Options.Tameable", false);
      this.tamed = mc.getBoolean("Options.Tamed", false);
      this.variant = mc.getString(new String[]{"Options.Variant", "Options.CatType"}, null);
      String collarColor = mc.getString(new String[]{"Options.Color", "Options.CollarColor"}, null);
      if (collarColor != null) {
         try {
            this.collarColor = DyeColor.valueOf(collarColor.toUpperCase());
         } catch (Throwable e) {
         }
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Cat e = (Cat)entity;
      e.setTamed(this.tamed);
      if (this.variant != null) {
         this.getPlugin().getVolatileCodeHandler().getCompatibilityHandler().setCatVariant(e, this.variant);
      }

      if (this.collarColor != null) {
         e.setCollarColor(this.collarColor);
      }

      return e;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJjMWU4MWZmMDNlODJhM2U3MWUwY2Q1ZmJlYzYwN2UxMTM2MTA4OWFhNDdmMjkwZDQ2YzhhMmMwNzQ2MGQ5MiJ9fX0";
   }
}
