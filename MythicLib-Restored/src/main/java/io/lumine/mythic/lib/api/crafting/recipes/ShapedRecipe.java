package io.lumine.mythic.lib.api.crafting.recipes;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeIngredient;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.ShapedIngredient;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShapedRecipe extends MythicRecipe implements VanillaBookableRecipe {
   int width = 0;
   int height = 0;

   public ShapedRecipe(@NotNull String name, @NotNull ArrayList<ShapedIngredient> ingredients) {
      super(name, new ArrayList<>(ingredients));
      this.recalculateSize();
   }

   public static ShapedRecipe single(@NotNull String name, @NotNull ProvidedUIFilter... poofs) {
      ShapedIngredient result = new ShapedIngredient(new MythicIngredient(name, poofs), 0, 0);
      return new ShapedRecipe(name, result);
   }

   public static ShapedRecipe unsharpen(@NotNull ShapedRecipe sharpedRecipe) {
      int trueMinWidth = sharpedRecipe.getWidth();
      int trueMinHeight = sharpedRecipe.getHeight();
      int trueMaxWidth = 0;
      int trueMaxHeight = 0;

      for (MythicRecipeIngredient ingredient : sharpedRecipe.getIngredients()) {
         if (ingredient != null) {
            ProvidedUIFilter poof = ingredient.getIngredient().getRandomSubstitute(false);
            if (poof != null && !poof.isAir()) {
               ShapedIngredient ing = (ShapedIngredient)ingredient;
               int cH = -ing.getVerticalOffset();
               int cW = ing.getHorizontalOffset();
               if (trueMinWidth > cW) {
                  trueMinWidth = cW;
               }

               if (trueMinHeight > cH) {
                  trueMinHeight = cH;
               }

               if (trueMaxWidth < cW) {
                  trueMaxWidth = cW;
               }

               if (trueMaxHeight < cH) {
                  trueMaxHeight = cH;
               }
            }
         }
      }

      ArrayList<ShapedIngredient> accepted = new ArrayList<>();

      for (int w = trueMinWidth; w <= trueMaxWidth; w++) {
         for (int h = trueMinHeight; h <= trueMaxHeight; h++) {
            ShapedIngredient found = sharpedRecipe.getIngredientAt(w, -h);
            if (found != null) {
               ShapedIngredient edited = found.clone();
               edited.setHorizontalOffset(edited.getHorizontalOffset() - trueMinWidth);
               edited.setVerticalOffset(edited.getVerticalOffset() + trueMinHeight);
               accepted.add(edited);
            }
         }
      }

      return new ShapedRecipe(sharpedRecipe.getName(), accepted);
   }

   public ShapedRecipe(@NotNull String name, @NotNull ShapedIngredient... ingredients) {
      super(name, ingredients);
   }

   @NotNull
   @Override
   public MythicRecipeStation getType() {
      return MythicRecipeStation.WORKBENCH;
   }

   @Override
   public void clearIngredients() {
      super.clearIngredients();
      this.width = 0;
      this.height = 0;
   }

   @Override
   public void addIngredient(@NotNull MythicRecipeIngredient ingredient) {
      Validate.isTrue(ingredient instanceof ShapedIngredient, "Shaped Mythic Recipes can only work with Shaped Ingredients.");
      ShapedIngredient replaced = this.getIngredientAt(((ShapedIngredient)ingredient).getHorizontalOffset(), ((ShapedIngredient)ingredient).getVerticalOffset());
      this.mythicIngredients.remove(replaced);
      this.mythicIngredients.add(ingredient);
      ((ShapedIngredient)ingredient).linkToRecipe(this);
      int cH = -((ShapedIngredient)ingredient).getVerticalOffset();
      int cW = ((ShapedIngredient)ingredient).getHorizontalOffset();
      if (this.getWidth() <= cW) {
         this.width = cW + 1;
      }

      if (this.getHeight() <= cH) {
         this.height = cH + 1;
      }
   }

   public void recalculateSize() {
      int highestHeight = 0;
      int widestWidth = 0;

      for (MythicRecipeIngredient g : this.getIngredients()) {
         int cH = -((ShapedIngredient)g).getVerticalOffset();
         int cW = ((ShapedIngredient)g).getHorizontalOffset();
         if (cW > widestWidth) {
            widestWidth = cW;
         }

         if (cH > highestHeight) {
            highestHeight = cH;
         }
      }

      this.width = widestWidth + 1;
      this.height = highestHeight + 1;
   }

   @Nullable
   public ShapedIngredient getIngredientAt(int w, int h) {
      for (MythicRecipeIngredient ingredient : this.getIngredients()) {
         ShapedIngredient shaped = (ShapedIngredient)ingredient;
         if (shaped.getHorizontalOffset() == w && shaped.getVerticalOffset() == h) {
            return shaped;
         }
      }

      return null;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   @NotNull
   public ArrayList<ArrayList<ShapedIngredient>> permute(int width, int height) {
      Integer criticalWidth = null;
      Integer criticalHeight = null;
      ArrayList<ArrayList<ShapedIngredient>> ret = new ArrayList<>();

      for (int w0 = 0; w0 <= width; w0++) {
         if (criticalWidth == null || w0 < criticalWidth) {
            for (int h0 = 0; h0 <= height; h0++) {
               if (criticalHeight == null || h0 < criticalHeight) {
                  ArrayList<ShapedIngredient> iterationResult = new ArrayList<>();
                  boolean cancelled = false;

                  for (MythicRecipeIngredient ingredient : this.getIngredients()) {
                     ShapedIngredient adapted = ((ShapedIngredient)ingredient).adapt(width, height, w0, -h0);
                     if (adapted == null) {
                        boolean ow = ((ShapedIngredient)ingredient).overflowsWidthFromOffset(width, w0);
                        boolean oh = ((ShapedIngredient)ingredient).overflowsHeightFromOffset(height, -h0);
                        if (ow) {
                           criticalWidth = w0;
                        }

                        if (oh) {
                           criticalHeight = h0;
                        }

                        cancelled = true;
                        break;
                     }

                     iterationResult.add(adapted);
                  }

                  if (!cancelled) {
                     ret.add(iterationResult);
                  }
               }
            }
         }
      }

      return ret;
   }

   @Nullable
   @Override
   public MythicRecipeInventory matches(@NotNull MythicRecipeInventory inventory, @Nullable Ref<Integer> timesToCompletion) {
      ArrayList<ShapedRecipe.CheckedSlot> slots = new ArrayList<>();
      if (inventory.getWidth() >= this.getWidth() && inventory.getHeight() >= this.getHeight()) {
         for (ArrayList<ShapedIngredient> combination : this.permute(inventory.getWidth(), inventory.getHeight())) {
            slots.clear();

            for (int c = 0; c < combination.size(); c++) {
               ShapedIngredient ingredient = combination.get(c);
               ItemStack found = inventory.getItemAt(ingredient.getHorizontalOffset(), ingredient.getVerticalOffset());
               if (found == null) {
                  if (!ingredient.getIngredient().acceptsAir()) {
                     return null;
                  }

                  found = new ItemStack(Material.AIR);
               }

               if (!ingredient.matches(found)) {
                  break;
               }

               if (c + 1 == combination.size()) {
                  slots.add(new ShapedRecipe.CheckedSlot(ingredient.getHorizontalOffset(), ingredient.getVerticalOffset()));

                  for (ShapedRecipe.CheckedSlot oSlot : ShapedRecipe.CheckedSlot.getFrom(inventory)) {
                     boolean contained = false;

                     for (ShapedRecipe.CheckedSlot sl : slots) {
                        if (oSlot.getWidth() == sl.getWidth() && oSlot.getHeight() == sl.getHeight()) {
                           contained = true;
                           break;
                        }
                     }

                     if (!contained) {
                        ItemStack item = inventory.getItemAt(oSlot.getWidth(), oSlot.getHeight());
                        if (!SilentNumbers.isAir(item)) {
                           return null;
                        }
                     }
                  }

                  MythicRecipeInventory result = inventory.clone();
                  int lowestTimes = 32767;

                  for (ShapedIngredient item : combination) {
                     ItemStack foundResult = result.getItemAt(item.getHorizontalOffset(), item.getVerticalOffset());
                     if (foundResult == null) {
                        MythicLib.plugin
                           .getLogger()
                           .log(
                              Level.SEVERE,
                              FriendlyFeedbackProvider.quickForConsole(
                                 FFPMythicLib.get(),
                                 "$fCustom recipe could not succeed: $bExpected item at offsets $r{0}h {1}w$b in order so subtract $u{2}$b. ",
                                 String.valueOf(item.getVerticalOffset()),
                                 String.valueOf(item.getHorizontalOffset()),
                                 String.valueOf(item.getAmountOfSuccess())
                              )
                           );
                        return null;
                     }

                     int amount = foundResult.getAmount();
                     int newAmount = amount - item.getAmountOfSuccess();
                     int times = SilentNumbers.floor((double)amount / item.getAmountOfSuccess());
                     foundResult.setAmount(newAmount);
                     if (lowestTimes > times) {
                        lowestTimes = times;
                     }
                  }

                  Ref.setValue(timesToCompletion, lowestTimes);
                  return result;
               }

               slots.add(new ShapedRecipe.CheckedSlot(ingredient.getHorizontalOffset(), ingredient.getVerticalOffset()));
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @NotNull
   @Override
   public Recipe getBukkitRecipe(@NotNull NamespacedKey recipeName, @NotNull ItemStack recipeResult) throws IllegalArgumentException {
      org.bukkit.inventory.ShapedRecipe ret = new org.bukkit.inventory.ShapedRecipe(recipeName, recipeResult);
      HashMap<Integer, HashMap<Integer, ShapedIngredient>> recipeContents = new HashMap<>();
      HashMap<Integer, String> rowStrings = new HashMap<>();
      int greatestH = 0;
      int greatestW = 0;

      for (int h = 0; h < this.getHeight() && h < 3; h++) {
         char[] chars;
         switch (h) {
            case 0:
               chars = new char[]{'A', 'B', 'C'};
               break;
            case 1:
               chars = new char[]{'D', 'E', 'F'};
               break;
            default:
               chars = new char[]{'G', 'H', 'I'};
         }

         HashMap<Integer, ShapedIngredient> rowContents = new HashMap<>();
         StringBuilder rowString = new StringBuilder();

         for (int w = 0; w < this.getWidth() && w < 3; w++) {
            ShapedIngredient get = this.getIngredientAt(w, -h);
            rowContents.put(w, get);
            greatestW = w;
            if (get == null || get.getIngredient().getSubstitutes().size() == 1 && get.getIngredient().getSubstitutes().get(0).isAir()) {
               rowString.append(" ");
            } else {
               rowString.append(chars[w]);
            }
         }

         String row = rowString.toString();
         boolean notBlanc = false;
         char[] chars2 = row.toCharArray();
         int length = chars2.length;
         int index = 0;

         while (true) {
            if (index < length) {
               char c = chars2[index];
               if (c == ' ') {
                  index++;
                  continue;
               }

               notBlanc = true;
            }

            if (notBlanc) {
               rowStrings.put(h, rowString.toString());
               recipeContents.put(h, rowContents);
               greatestH = h;
            }
            break;
         }
      }

      String[] rowsBaked = new String[greatestH + 1];

      for (Integer r : rowStrings.keySet()) {
         rowsBaked[r] = rowStrings.get(r);
      }

      for (int s = 0; s < rowsBaked.length; s++) {
         if (rowsBaked[s] == null) {
            switch (greatestW) {
               default:
                  rowsBaked[s] = " ";
               case 1:
                  rowsBaked[s] = "  ";
               case 2:
                  rowsBaked[s] = "   ";
            }
         }
      }

      ret.shape(rowsBaked);

      for (int h = 0; h < this.getHeight() && h < 3; h++) {
         char[] chars;
         switch (h) {
            case 0:
               chars = new char[]{'A', 'B', 'C'};
               break;
            case 1:
               chars = new char[]{'D', 'E', 'F'};
               break;
            default:
               chars = new char[]{'G', 'H', 'I'};
         }

         HashMap<Integer, ShapedIngredient> rowContent = recipeContents.get(h);
         if (rowContent != null) {
            for (int w = 0; w < this.getWidth() && w < 3; w++) {
               ShapedIngredient ingredient = rowContent.get(w);
               if (ingredient != null) {
                  try {
                     ret.setIngredient(chars[w], ingredient.toBukkit());
                  } catch (IllegalArgumentException exception) {
                  }
               }
            }
         }
      }

      return ret;
   }

   static class CheckedSlot {
      final int width;
      final int height;

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      CheckedSlot(int width, int height) {
         this.width = width;
         this.height = height;
      }

      public static ArrayList<ShapedRecipe.CheckedSlot> getFrom(@NotNull MythicRecipeInventory inventory) {
         ArrayList<ShapedRecipe.CheckedSlot> ret = new ArrayList<>();

         for (int w = 0; w < inventory.getWidth(); w++) {
            for (int h = 0; h < inventory.getHeight(); h++) {
               ret.add(new ShapedRecipe.CheckedSlot(w, -h));
            }
         }

         return ret;
      }
   }
}
