package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.ItemStack;

public class BukkitShulker extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.SHULKER_BOX).build();
   @Getter
   private DyeColor color;

   @Override
   public EntityType getBukkitType() {
      return EntityType.SHULKER;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      try {
         this.color = DyeColor.valueOf(mc.getString("Options.Color", "PURPLE"));
      } catch (Exception e) {
         MythicLogger.errorGenericConfig("Invalid dye color specified");
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Shulker e = (Shulker)entity;
      e.setColor(this.color);
      return e;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}
