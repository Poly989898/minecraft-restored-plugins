package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitWitherSkeleton extends BukkitEntityType {
   private static final String headString = "";
   private static final ItemStack head = ItemFactory.of(Material.WITHER_SKELETON_SKULL).build();

   @Override
   public EntityType getBukkitType() {
      return EntityType.WITHER_SKELETON;
   }

   @Override
   public int getHeight() {
      return 3;
   }
}
