package io.lumine.mythic.lib.api.crafting.recipes;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.ShapelessItem;
import io.lumine.mythic.lib.api.crafting.ingredients.ShapelessRequester;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShapelessRecipe extends MythicRecipe implements VanillaBookableRecipe {
   public ShapelessRecipe(@NotNull String name, @NotNull ArrayList<MythicRecipeIngredient> ingredients) {
      super(name, ingredients);
   }

   public ShapelessRecipe(@NotNull String name, @NotNull MythicRecipeIngredient... ingredients) {
      super(name, ingredients);
   }

   @Override
   public void addIngredient(@NotNull MythicRecipeIngredient ingredient) {
      this.mythicIngredients.add(ingredient);
   }

   @NotNull
   @Override
   public MythicRecipeStation getType() {
      return MythicRecipeStation.WORKBENCH;
   }

   @Nullable
   @Override
   public MythicRecipeInventory matches(@NotNull MythicRecipeInventory inventory, @Nullable Ref<Integer> timesToCompletion) {
      ArrayList<MythicIngredient> ingredients = new ArrayList<>();

      for (MythicRecipeIngredient rIngredient : this.getIngredients()) {
         ingredients.add(rIngredient.getIngredient());
      }

      ArrayList<ShapelessItem> result = this.matches(this.pool(inventory), ingredients);
      if (result != null) {
         MythicRecipeInventory ret = inventory.clone();
         int leastTimes = 32767;

         for (ShapelessItem resultItem : result) {
            ItemStack stacc = ret.getItemAt(resultItem.getOriginalWidthCoord(), resultItem.getOriginalHeightCoord());
            Validate.isTrue(stacc != null, "Original coordinates mismatch in shapeless recipe: Result item cannot point to an original null");
            if (timesToCompletion != null) {
               int originalAmount = stacc.getAmount();
               int finalAmount = resultItem.getAmount();
               int difference = originalAmount - finalAmount;
               int times = SilentNumbers.floor((double)originalAmount / difference);
               if (leastTimes > times) {
                  leastTimes = times;
               }
            }

            stacc.setAmount(resultItem.getAmount());
         }

         Ref.setValue(timesToCompletion, leastTimes);
         return ret;
      } else {
         return null;
      }
   }

   @Nullable
   public ArrayList<ShapelessItem> matches(@NotNull ArrayList<ShapelessItem> pool, @NotNull ArrayList<MythicIngredient> unmatchedIngredients) {
      ArrayList<ShapelessRequester> dibbers = new ArrayList<>();
      int rOID = 0;

      for (MythicIngredient checker : unmatchedIngredients) {
         ShapelessRequester check = new ShapelessRequester(rOID, checker);
         rOID++;
         dibbers.add(check);

         for (ShapelessItem ingredient : pool) {
            Ref<ArrayList<ProvidedUIFilter>> match = new Ref<>();
            FriendlyFeedbackProvider ffp = null;
            if (check.getIngredient().matches(ingredient.getStack(), true, match, true, ffp)) {
               Collections.sort(match.getValue());
               check.dib(ingredient, match.getValue().get(0));
            }
         }
      }

      for (ShapelessItem ingredient : pool) {
         if (ingredient.getRequesters().size() == 0) {
            return null;
         }
      }

      ArrayList<ShapelessRequester> ambiguouslyFulfilled = new ArrayList<>();

      for (ShapelessRequester requester : dibbers) {
         if (!requester.firstClaim()) {
            return null;
         }

         if (!requester.isFulfilled()) {
            ambiguouslyFulfilled.add(requester);
         }
      }

      if (ambiguouslyFulfilled.size() == 0) {
         return pool;
      }

      ShapelessRequester chosen = ambiguouslyFulfilled.get(0);
      ArrayList<ArrayList<ShapelessItem>> alternativePools = chosen.secondClaim(ShapelessItem.completeCloneList(pool));
      Validate.isTrue(alternativePools.size() > 0, "Fatal Error when evaluating shapeless recipe: Alternative Pools Array Size Equals 0, which is ridiculous.");
      ArrayList<MythicIngredient> alternativeIngredients = new ArrayList<>();

      for (int r = 1; r < ambiguouslyFulfilled.size(); r++) {
         ShapelessRequester observed = ambiguouslyFulfilled.get(r);
         alternativeIngredients.add(observed.getIngredient());
      }

      for (ArrayList<ShapelessItem> alternativePool : alternativePools) {
         ArrayList<ShapelessItem> ret = this.matches(alternativePool, alternativeIngredients);
         if (ret != null) {
            for (ShapelessItem alternative : ret) {
               for (ShapelessItem original : pool) {
                  if (alternative.getOperationalIdentifier() == original.getOperationalIdentifier()) {
                     int currentA = original.getTaken();
                     int finalA = alternative.getTaken();
                     int deltaA = finalA - currentA;
                     original.take(deltaA);
                  }
               }
            }

            return pool;
         }
      }

      return null;
   }

   @NotNull
   public ArrayList<ShapelessItem> pool(@NotNull MythicRecipeInventory inventory) {
      ArrayList<ShapelessItem> ret = new ArrayList<>();
      int oid = 0;

      for (int w = 0; w < inventory.getWidth(); w++) {
         for (int h = 0; h < inventory.getHeight(); h++) {
            ItemStack at = inventory.getItemAt(w, -h);
            if (!SilentNumbers.isAir(at)) {
               ret.add(new ShapelessItem(oid, at, w, -h));
               oid++;
            }
         }
      }

      return ret;
   }

   @NotNull
   @Override
   public Recipe getBukkitRecipe(@NotNull NamespacedKey recipeName, @NotNull ItemStack recipeResult) throws IllegalArgumentException {
      org.bukkit.inventory.ShapelessRecipe ret = new org.bukkit.inventory.ShapelessRecipe(recipeName, recipeResult);

      for (MythicRecipeIngredient ingredient : this.getIngredients()) {
         if (ingredient != null) {
            ret.addIngredient(ingredient.toBukkit());
         }
      }

      return ret;
   }
}
