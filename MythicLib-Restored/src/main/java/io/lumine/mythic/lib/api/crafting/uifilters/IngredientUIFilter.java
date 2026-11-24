package io.lumine.mythic.lib.api.crafting.uifilters;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicIngredient;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IngredientUIFilter implements UIFilter {
   static IngredientUIFilter global;

   @NotNull
   @Override
   public String getIdentifier() {
      return "i";
   }

   @Override
   public boolean matches(@NotNull ItemStack item, @NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isValid(argument, data, ffp)) {
         return false;
      }

      if (this.cancelMatch(item, ffp)) {
         return false;
      }

      MythicIngredient ingredient = MythicIngredient.get(argument);
      return ingredient.matches(item, ffp);
   }

   @Override
   public boolean isValid(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (MythicIngredient.get(argument) != null) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Mythic Ingredient found, $svalidated$b. ");
         return true;
      } else {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "Mythic Ingredient '$u{0}$b' not found, $frejected$b. ", argument);
         return false;
      }
   }

   @NotNull
   @Override
   public ArrayList<String> tabCompleteArgument(@NotNull String current) {
      return SilentNumbers.smartFilter(MythicIngredient.getEnabledIngredients(), current, true);
   }

   @NotNull
   @Override
   public ArrayList<String> tabCompleteData(@NotNull String argument, @NotNull String current) {
      return SilentNumbers.toArrayList("0", "(this_is_not_checked,_write_anything)");
   }

   @Override
   public boolean fullyDefinesItem() {
      return true;
   }

   @Nullable
   @Override
   public ItemStack getItemStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isValid(argument, data, ffp)) {
         return null;
      }

      MythicIngredient ingredient = MythicIngredient.get(argument);
      return ingredient.getRandomSubstituteItem(null);
   }

   @NotNull
   @Override
   public ItemStack getDisplayStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isValid(argument, data, ffp)) {
         return ItemFactory.of(Material.BARRIER).name("§cInvalid MythicIngredient").build();
      }

      MythicIngredient ingredient = MythicIngredient.get(argument);
      return ingredient.getRandomDisplayItem(null);
   }

   @NotNull
   @Override
   public ArrayList<String> getDescription(@NotNull String argument, @NotNull String data) {
      if (!this.isValid(argument, data, null)) {
         return SilentNumbers.toArrayList("Ingredient '$u" + argument + "$b' is $finvalid$b.");
      }

      MythicIngredient ingredient = MythicIngredient.get(argument);
      ArrayList<String> ret = new ArrayList<>();
      ret.add("$sAny of the following:");

      for (ProvidedUIFilter puff : ingredient.getSubstitutes()) {
         String prefix = " $r► $b";
         if (puff.hasAmount()) {
            prefix = " " + puff.getAmountRange().toStringColored() + "x $b";
         }

         StringBuilder tab = new StringBuilder();

         for (int s = 0; s < prefix.length(); s++) {
            tab.append(" ");
         }

         boolean first = true;
         String tabulation = tab.toString();

         for (String str : puff.getDescription()) {
            if (first) {
               ret.add(prefix + str);
               first = false;
            } else {
               ret.add(tabulation + str);
            }
         }
      }

      return ret;
   }

   @Override
   public boolean determinateGeneration() {
      return false;
   }

   @Override
   public boolean partialDeterminateGeneration(@NotNull String argument, @NotNull String data) {
      if (!this.isValid(argument, data, null)) {
         return false;
      }

      MythicIngredient ingredient = MythicIngredient.get(argument);
      return ingredient.getSubstitutes().size() <= 1;
   }

   @NotNull
   @Override
   public String getSourcePlugin() {
      return "MythicLib";
   }

   @NotNull
   @Override
   public String getFilterName() {
      return "MythicIngredient";
   }

   @NotNull
   @Override
   public String exampleArgument() {
      return "wood_planks";
   }

   @NotNull
   @Override
   public String exampleData() {
      return "0";
   }

   public static void register() {
      ArrayList<ProvidedUIFilter> planks = new ArrayList<>();
      planks.add(UIFilterManager.getUIFilter("v", Material.OAK_PLANKS.toString(), "0", null, null));
      planks.add(UIFilterManager.getUIFilter("v", Material.SPRUCE_PLANKS.toString(), "0", null, null));
      planks.add(UIFilterManager.getUIFilter("v", Material.BIRCH_PLANKS.toString(), "0", null, null));
      planks.add(UIFilterManager.getUIFilter("v", Material.JUNGLE_PLANKS.toString(), "0", null, null));
      planks.add(UIFilterManager.getUIFilter("v", Material.ACACIA_PLANKS.toString(), "0", null, null));
      planks.add(UIFilterManager.getUIFilter("v", Material.DARK_OAK_PLANKS.toString(), "0", null, null));
      MythicIngredient plank = new MythicIngredient("wood_planks", planks);
      MythicIngredient.enable(plank);
      global = new IngredientUIFilter();
      UIFilterManager.registerUIFilter(global);
   }

   @NotNull
   public static IngredientUIFilter get() {
      return global;
   }
}
