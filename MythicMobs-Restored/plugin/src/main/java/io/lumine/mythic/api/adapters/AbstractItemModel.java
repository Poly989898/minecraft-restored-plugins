package io.lumine.mythic.api.adapters;

import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import java.util.OptionalInt;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class AbstractItemModel {
   private NamespacedKey itemModel;
   private int model = 0;

   public static AbstractItemModel of(String modelInput) {
      return new AbstractItemModel(modelInput);
   }

   private AbstractItemModel(String modelInput) {
      OptionalInt maybeNumber = Numbers.parseInteger(modelInput);
      if (maybeNumber.isPresent()) {
         this.model = maybeNumber.getAsInt();
         this.itemModel = null;
      } else {
         if (!modelInput.contains(":")) {
            modelInput = "minecraft:" + modelInput;
         }

         this.itemModel = NamespacedKey.fromString(modelInput.toLowerCase());
      }
   }

   public ItemStack apply(ItemStack stack) {
      return this.apply(ItemFactory.of(stack)).build();
   }

   public ItemFactory apply(ItemFactory factory) {
      return this.itemModel != null ? factory.itemModel(this.itemModel) : factory.customModelData(this.model);
   }
}
