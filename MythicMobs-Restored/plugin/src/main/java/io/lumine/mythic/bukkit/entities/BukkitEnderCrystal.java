package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitEnderCrystal extends BukkitEntityType {
   @Getter
   private EntityType bukkitType = EntityType.END_CRYSTAL;
   private static final int HEIGHT = 2;
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.END_CRYSTAL).build();

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
