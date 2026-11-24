package io.lumine.mythic.lib.api.crafting.ingredients;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.uifilters.IngredientUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.jetbrains.annotations.NotNull;

public class MythicRecipeIngredient {
   @NotNull
   final MythicIngredient ingredient;

   @NotNull
   public MythicIngredient getIngredient() {
      return this.ingredient;
   }

   public MythicRecipeIngredient(@NotNull MythicIngredient ingredient) {
      this.ingredient = ingredient;
   }

   public MythicRecipeIngredient(@NotNull ProvidedUIFilter ingredient) throws IllegalArgumentException {
      if (ingredient.getParent() instanceof IngredientUIFilter) {
         MythicIngredient mythicIngredient = MythicIngredient.get(ingredient.getArgument());
         if (mythicIngredient == null) {
            throw new IllegalArgumentException("$bIngredient is not loaded ($u" + ingredient.toString() + "$b).");
         }

         this.ingredient = mythicIngredient;
      } else {
         if (ingredient.getParent().useInventoryMatch()) {
            throw new IllegalArgumentException("$bMythic Ingredients don't support filters with inventory match ($u" + ingredient.toString() + "$b).");
         }

         this.ingredient = new MythicIngredient(ingredient.toString(), ingredient);
      }
   }

   @NotNull
   public RecipeChoice toBukkit() {
      ArrayList<ItemStack> choices = new ArrayList<>();

      for (ProvidedUIFilter substitute : this.getIngredient().getSubstitutes()) {
         FriendlyFeedbackProvider ffp = new FriendlyFeedbackProvider(FFPMythicLib.get());
         ffp.activatePrefix(true, "Ingredient §b" + this.getIngredient().getName());
         choices.add(substitute.getDisplayStack(ffp));
         ffp.sendTo(FriendlyFeedbackCategory.ERROR, MythicLib.plugin.getServer().getConsoleSender());
         ffp.sendTo(FriendlyFeedbackCategory.FAILURE, MythicLib.plugin.getServer().getConsoleSender());
      }

      return new ExactChoice(choices);
   }
}
