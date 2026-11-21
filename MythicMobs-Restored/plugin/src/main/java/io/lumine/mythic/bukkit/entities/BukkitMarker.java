package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitMarker extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.STRUCTURE_VOID).build();

   @Override
   public EntityType getBukkitType() {
      return EntityType.MARKER;
   }

   @Override
   public int getHeight() {
      return 0;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
