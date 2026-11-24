package io.lumine.mythic.lib.api.crafting.uifilters;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.recipes.MythicCraftingManager;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipe;
import io.lumine.mythic.lib.api.crafting.recipes.ShapelessRecipe;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeUIFilter implements UIFilter {
   static RecipeUIFilter global;

   @NotNull
   @Override
   public String getIdentifier() {
      return "r";
   }

   @NotNull
   static ItemStack getFromInventory(@NotNull Inventory inven, int slot) {
      ItemStack target = inven.getItem(slot);
      if (target == null) {
         target = new ItemStack(Material.CAULDRON);
      }

      return target;
   }

   @Override
   public boolean matches(
      @NotNull Inventory inventory,
      int targetSlot,
      int width,
      int height,
      @NotNull String argument,
      @NotNull String data,
      @Nullable QuickNumberRange amount,
      @Nullable FriendlyFeedbackProvider ffp
   ) {
      if (!this.isValid(argument, data, ffp)) {
         return false;
      }

      if (this.cancelMatch(getFromInventory(inventory, targetSlot), ffp)) {
         return false;
      }

      MythicRecipe recipe = MythicCraftingManager.getLoadedRecipe(argument);
      MythicRecipeInventory extract = new MythicRecipeInventory();

      for (int h = 0; h < height; h++) {
         ItemStack[] row = new ItemStack[width];

         for (int w = 0; w < width; w++) {
            int current = targetSlot + w + h * width;
            if (inventory.getSize() > current) {
               ItemStack found = inventory.getItem(current);
               if (found != null) {
                  found = found.clone();
               }

               row[w] = found;
            }
         }

         extract.addRow(row);
      }

      return recipe.matches(extract, null) != null;
   }

   @Override
   public boolean matches(@NotNull ItemStack item, @NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      this.cancelMatch(item, ffp);
      return false;
   }

   @Override
   public boolean isValid(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      return MythicCraftingManager.getLoadedRecipe(argument) != null;
   }

   @NotNull
   @Override
   public ArrayList<String> tabCompleteArgument(@NotNull String current) {
      return SilentNumbers.smartFilter(MythicCraftingManager.getLoadedRecipes(), current, true);
   }

   @NotNull
   @Override
   public ArrayList<String> tabCompleteData(@NotNull String argument, @NotNull String current) {
      return SilentNumbers.toArrayList("0", "(this_is_not_checked,_write_anything)");
   }

   @Override
   public boolean fullyDefinesItem() {
      return false;
   }

   @Override
   public boolean useInventoryMatch() {
      return true;
   }

   @Nullable
   @Override
   public ItemStack getItemStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      return null;
   }

   @NotNull
   @Override
   public ItemStack getDisplayStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      return ItemFactory.of(Material.CRAFTING_TABLE).name("§aMythicRecipe §e " + argument).lore(this.getDescription(argument, data)).build();
   }

   @NotNull
   @Override
   public ArrayList<String> getDescription(@NotNull String argument, @NotNull String data) {
      return !this.isValid(argument, data, null)
         ? SilentNumbers.toArrayList("Recipe '$u" + argument + "$b' is $finvalid$b.")
         : SilentNumbers.chop("Checks that the area contains the shaped recipe §b§o" + argument + "§7§o in correct order and amounts.", 55, "§7§o");
   }

   @Override
   public boolean determinateGeneration() {
      return true;
   }

   @NotNull
   @Override
   public String getSourcePlugin() {
      return "MythicLib";
   }

   @NotNull
   @Override
   public String getFilterName() {
      return "MythicRecipe";
   }

   @NotNull
   @Override
   public String exampleArgument() {
      return "mysterious_sample_recipe";
   }

   @NotNull
   @Override
   public String exampleData() {
      return "0";
   }

   public static void register() {
      MythicIngredient ingredient = MythicIngredient.get("wood_planks");
      Validate.isTrue(ingredient != null, "RecipeUIFilter must enable after the IngredientUIFilter does, so that we can use that ingredient.");
      ShapelessRecipe recipe = new ShapelessRecipe("mysterious_sample_recipe", new MythicRecipeIngredient(ingredient));
      recipe.load();
      global = new RecipeUIFilter();
      UIFilterManager.registerUIFilter(global);
      recipe.unload();
   }

   @NotNull
   public static RecipeUIFilter get() {
      return global;
   }
}
