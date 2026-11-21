package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitAreaEffectCloud extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.LINGERING_POTION).build();

   @Override
   public EntityType getBukkitType() {
      return EntityType.AREA_EFFECT_CLOUD;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public Entity applyOptions(Entity entity) {
      return (AreaEffectCloud)entity;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
