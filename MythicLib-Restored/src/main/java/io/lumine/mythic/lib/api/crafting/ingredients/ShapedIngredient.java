package io.lumine.mythic.lib.api.crafting.ingredients;

import io.lumine.mythic.lib.api.crafting.recipes.ShapedRecipe;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShapedIngredient extends MythicRecipeIngredient implements Cloneable {
   @Nullable
   ShapedRecipe recipe = null;
   int verticalOffset = 0;
   int horizontalOffset = 0;
   public int amountOfSuccess = 0;

   public ShapedIngredient(@NotNull MythicIngredient ingredient, int horizontalOffset, int verticalOffset) {
      super(ingredient);
      this.setHorizontalOffset(horizontalOffset);
      this.setVerticalOffset(verticalOffset);
   }

   public ShapedIngredient(@NotNull ProvidedUIFilter ingredient, int horizontalOffset, int verticalOffset) {
      super(ingredient);
      this.setHorizontalOffset(horizontalOffset);
      this.setVerticalOffset(verticalOffset);
   }

   public void linkToRecipe(@NotNull ShapedRecipe recipe) {
      this.recipe = recipe;
   }

   @Nullable
   ShapedRecipe getRecipe() {
      return this.recipe;
   }

   public int getVerticalOffset() {
      return this.verticalOffset;
   }

   public void setVerticalOffset(int verticalOffset) {
      this.verticalOffset = verticalOffset;
      if (this.getRecipe() != null) {
         this.getRecipe().recalculateSize();
      }
   }

   public int getHorizontalOffset() {
      return this.horizontalOffset;
   }

   public void setHorizontalOffset(int horizontalOffset) {
      this.horizontalOffset = horizontalOffset;
      if (this.getRecipe() != null) {
         this.getRecipe().recalculateSize();
      }
   }

   public int getAmountOfSuccess() {
      return this.amountOfSuccess;
   }

   public boolean matches(@NotNull ItemStack item) {
      Ref<ArrayList<ProvidedUIFilter>> match = new Ref<>();
      FriendlyFeedbackProvider ffp = null;
      if (this.getIngredient().matches(item, false, match, true, ffp)) {
         ArrayList<ProvidedUIFilter> matched = match.getValue();
         assert matched != null;
         Collections.sort(matched);
         ProvidedUIFilter used = match.getValue().get(0);
         this.amountOfSuccess = used.getAmount(0);
         return true;
      } else {
         return false;
      }
   }

   public ShapedIngredient clone() {
      try {
         super.clone();
      } catch (CloneNotSupportedException var2) {
      }

      return new ShapedIngredient(this.getIngredient().clone(), this.getHorizontalOffset(), this.getVerticalOffset());
   }

   @Nullable
   public ShapedIngredient adapt(int width, int height) {
      return this.adapt(width, height, 0, 0);
   }

   @Nullable
   public ShapedIngredient adapt(int width, int height, int w0, int h0) throws IllegalArgumentException {
      if (width < 0) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(
               FFPMythicLib.get(), "Width of inventories $fcannot be negative$b. Provided $u{0}$b. ", String.valueOf(width)
            )
         );
      } else if (height < 0) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(
               FFPMythicLib.get(), "Height of inventories $fcannot be negative$b. Provided $u{0}$b. ", String.valueOf(height)
            )
         );
      } else if (w0 < 0) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(
               FFPMythicLib.get(), "Width offset for positional recipe ingredients $fcannot be negative$b. Provided $u{0}$b. ", String.valueOf(w0)
            )
         );
      } else if (h0 > 0) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(
               FFPMythicLib.get(), "Height offset for positional recipe ingredients $fcannot be positive$b. Provided $u{0}$b. ", String.valueOf(h0)
            )
         );
      } else if (this.getHorizontalOffset() < 0) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(
               FFPMythicLib.get(),
               "Horizontal offset for positional recipe ingredients $fcannot be negative$b. Provided $u{0}$b. ",
               String.valueOf(this.getHorizontalOffset())
            )
         );
      } else if (this.getVerticalOffset() > 0) {
         throw new IllegalArgumentException(
            FriendlyFeedbackProvider.quickForConsole(
               FFPMythicLib.get(),
               "Vertical offset for positional recipe ingredients $fcannot be positive$b. Provided $u{0}$b. ",
               String.valueOf(this.getVerticalOffset())
            )
         );
      } else {
         return !this.overflowsWidthFromOffset(width, w0) && !this.overflowsHeightFromOffset(height, h0)
            ? new ShapedIngredient(this.getIngredient().clone(), w0 + this.getHorizontalOffset(), h0 + this.getVerticalOffset())
            : null;
      }
   }

   public boolean overflowsWidthFromOffset(int width, int w0) {
      int expectedHorizontalPosition = w0 + this.getHorizontalOffset();
      return expectedHorizontalPosition >= width;
   }

   public boolean overflowsHeightFromOffset(int height, int h0) {
      int expectedVerticalPosition = h0 + this.getVerticalOffset();
      return -expectedVerticalPosition >= height;
   }
}
