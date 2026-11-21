package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitSalmon extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.SALMON_BUCKET).build();

   @Override
   public EntityType getBukkitType() {
      return EntityType.SALMON;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
