package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

public class BukkitSkeleton extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.SKELETON_SKULL).build();
   @Getter
   private boolean preventConversion = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.SKELETON;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.preventConversion = mc.getBoolean("Options.PreventConversion", MythicBukkit.inst().getConfiguration().getPreventConversionByDefault());
   }

   @Override
   public Entity applyOptions(Entity e) {
      Skeleton skeleton = (Skeleton)e;
      if (this.preventConversion) {
         skeleton.setConversionTime(Integer.MAX_VALUE);
      }

      return skeleton;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
