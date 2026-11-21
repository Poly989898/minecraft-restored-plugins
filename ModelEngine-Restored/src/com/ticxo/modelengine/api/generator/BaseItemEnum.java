package com.ticxo.modelengine.api.generator;

import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum BaseItemEnum {
   FILLED_MAP(BaseItemEnum.Cons.MAP_CONSUMER, Material.FILLED_MAP),
   LEATHER_BOOTS(BaseItemEnum.Cons.LEATHER_CONSUMER, Material.LEATHER_BOOTS),
   LEATHER_CHESTPLATE(BaseItemEnum.Cons.LEATHER_CONSUMER, Material.LEATHER_CHESTPLATE),
   LEATHER_HORSE_ARMOR(BaseItemEnum.Cons.LEATHER_CONSUMER, Material.LEATHER_HORSE_ARMOR),
   LEATHER_LEGGINGS(BaseItemEnum.Cons.LEATHER_CONSUMER, Material.LEATHER_LEGGINGS),
   LINGERING_POTION(BaseItemEnum.Cons.POTION_CONSUMER, Material.LINGERING_POTION),
   POTION(BaseItemEnum.Cons.POTION_CONSUMER, Material.POTION),
   SPLASH_POTION(BaseItemEnum.Cons.POTION_CONSUMER, Material.SPLASH_POTION),
   TIPPED_ARROW(BaseItemEnum.Cons.POTION_CONSUMER, Material.TIPPED_ARROW);

   private final BiConsumer<ItemMeta, Color> metaConsumer;
   @Getter
   private final Material material;

   public static BaseItemEnum get(String value) {
      try {
         return valueOf(value);
      } catch (IllegalArgumentException ignored) {
         return LEATHER_HORSE_ARMOR;
      }
   }

   public static BaseItemEnum fromMaterial(Material material) {
      return switch (material) {
         case FILLED_MAP -> FILLED_MAP;
         case LEATHER_BOOTS -> LEATHER_BOOTS;
         case LEATHER_CHESTPLATE -> LEATHER_CHESTPLATE;
         case LEATHER_HORSE_ARMOR -> LEATHER_HORSE_ARMOR;
         case LEATHER_LEGGINGS -> LEATHER_LEGGINGS;
         case LINGERING_POTION -> LINGERING_POTION;
         case POTION -> POTION;
         case SPLASH_POTION -> SPLASH_POTION;
         case TIPPED_ARROW -> TIPPED_ARROW;
         default -> null;
      };
   }

   public void color(ItemMeta meta, Color color) {
      this.metaConsumer.accept(meta, color);
   }

   public ItemStack create() {
      return new ItemStack(this.material);
   }

   public ItemStack create(Color color, int data) {
      ItemStack stack = this.create();
      ItemMeta meta = stack.getItemMeta();
      meta.setCustomModelData(data);
      this.color(meta, color);
      stack.setItemMeta(meta);
      return stack;
   }

   private static class Cons {
      private static final BiConsumer<ItemMeta, Color> MAP_CONSUMER = (meta, color) -> {
         MapMeta map = (MapMeta)meta;
         map.setColor(color);
      };
      private static final BiConsumer<ItemMeta, Color> LEATHER_CONSUMER = (meta, color) -> {
         LeatherArmorMeta leather = (LeatherArmorMeta)meta;
         leather.setColor(color);
      };
      private static final BiConsumer<ItemMeta, Color> POTION_CONSUMER = (meta, color) -> {
         PotionMeta potion = (PotionMeta)meta;
         potion.setColor(color);
      };
   }
}
