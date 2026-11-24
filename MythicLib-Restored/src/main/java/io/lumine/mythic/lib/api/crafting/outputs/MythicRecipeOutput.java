package io.lumine.mythic.lib.api.crafting.outputs;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.recipes.MythicCachedResult;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.VanillaInventoryMapping;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class MythicRecipeOutput {
   @NotNull
   public abstract MythicRecipeInventory applyDisplay(
      @NotNull MythicBlueprintInventory var1, @NotNull InventoryClickEvent var2, @NotNull VanillaInventoryMapping var3
   );

   public abstract void applyResult(
      @NotNull MythicRecipeInventory var1,
      @NotNull MythicBlueprintInventory var2,
      @NotNull MythicCachedResult var3,
      @NotNull InventoryClickEvent var4,
      @NotNull VanillaInventoryMapping var5,
      int var6
   );

   public void consumeIngredients(
      @NotNull MythicBlueprintInventory originalInventories,
      @NotNull MythicCachedResult cache,
      @NotNull Inventory inven,
      @NotNull VanillaInventoryMapping map,
      int times
   ) {
      MythicRecipeInventory main = originalInventories.getMainInventory();
      this.processInventory(main, cache.getResultOfOperation().getMainInventory(), times);
      map.applyToMainInventory(inven, main, false);

      for (String sideName : originalInventories.getSideInventoryNames()) {
         MythicRecipeInventory side = originalInventories.getSideInventory(sideName);
         if (cache.getResultOfOperation().hasSideInventories(sideName)) {
            MythicRecipeInventory sideResult = cache.getResultOfOperation().getSideInventory(sideName);
            this.processInventory(side, sideResult, times);
            map.applyToSideInventory(inven, side, sideName, false);
         }
      }
   }

   public void processInventory(@NotNull MythicRecipeInventory original, @NotNull MythicRecipeInventory overwrite, int multiplier) {
      for (int h = 0; h < original.getHeight(); h++) {
         for (int w = 0; w < original.getWidth(); w++) {
            ItemStack stack = overwrite.getItemAt(w, -h);
            if (stack != null) {
               ItemStack current = original.getItemAt(w, -h);
               if (current != null) {
                  int currentAmount = current.getAmount();
                  int targetAmount = stack.getAmount();
                  int difference = currentAmount - targetAmount;
                  if (current.isSimilar(stack)) {
                     int finalAmount = Math.max(currentAmount - difference * multiplier, 0);
                     current.setAmount(finalAmount);
                  } else {
                     int finalAmount = targetAmount * multiplier;
                     current = stack;
                     current.setAmount(finalAmount);
                  }

                  original.setItemAt(w, -h, current);
               }
            }
         }
      }
   }
}
