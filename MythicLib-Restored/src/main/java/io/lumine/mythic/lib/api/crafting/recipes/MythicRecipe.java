package io.lumine.mythic.lib.api.crafting.recipes;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.util.Ref;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MythicRecipe {
   @NotNull
   final ArrayList<MythicRecipeIngredient> mythicIngredients;
   @NotNull
   final String name;
   boolean live = false;

   public abstract void addIngredient(@NotNull MythicRecipeIngredient var1);

   @NotNull
   public ArrayList<MythicRecipeIngredient> getIngredients() {
      return this.mythicIngredients;
   }

   public void clearIngredients() {
      this.mythicIngredients.clear();
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   @NotNull
   public abstract MythicRecipeStation getType();

   public MythicRecipe(@NotNull String name, @NotNull ArrayList<MythicRecipeIngredient> ingredients) {
      this.name = name;
      this.mythicIngredients = ingredients;
   }

   public MythicRecipe(@NotNull String name, @NotNull MythicRecipeIngredient... ingredients) {
      this.name = name;
      this.mythicIngredients = new ArrayList<>();

      for (MythicRecipeIngredient ing : ingredients) {
         if (ing != null) {
            this.mythicIngredients.add(ing);
         }
      }
   }

   @Nullable
   public abstract MythicRecipeInventory matches(@NotNull MythicRecipeInventory var1, @Nullable Ref<Integer> var2);

   public void load() {
      MythicCraftingManager.loadRecipe(this);
   }

   public void unload() {
      MythicCraftingManager.unloadRecipe(this);
   }

   public boolean isLive() {
      return this.live;
   }

   protected void setLive(boolean lv) {
      this.live = lv;
   }
}
