package io.lumine.mythic.lib.api.crafting.uifilters;

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

public class VanillaUIFilter implements UIFilter {
   static ArrayList<String> materialNames = null;
   static VanillaUIFilter global;

   @NotNull
   @Override
   public String getIdentifier() {
      return "v";
   }

   @Override
   public boolean matches(@NotNull ItemStack item, @NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isValid(argument, data, ffp)) {
         return false;
      } else if (this.cancelMatch(item, ffp)) {
         return false;
      } else {
         Material mat = Material.valueOf(argument.toUpperCase().replace(" ", "_").replace("-", "_"));
         String materialName = mat.toString().toLowerCase().replace("_", " ");
         if (item.getType().equals(mat)) {
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Item material matched $s{0}$b. ", materialName);
            return true;
         } else {
            FriendlyFeedbackProvider.log(
               ffp,
               FriendlyFeedbackCategory.FAILURE,
               "Item material '$u{0}$b' failed to match expected material $f{1}$b. ",
               item.getType().toString().toLowerCase().replace("_", " "),
               materialName
            );
            return false;
         }
      }
   }

   @Override
   public boolean isValid(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      try {
         Material.valueOf(argument.toUpperCase().replace(" ", "_").replace("-", "_"));
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Material found, $svalidated$b. ");
         return true;
      } catch (IllegalArgumentException ignored) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "Expected a vanilla material instead of $u{0}$b. ", argument);
         return false;
      }
   }

   @NotNull
   @Override
   public ArrayList<String> tabCompleteArgument(@NotNull String current) {
      return SilentNumbers.smartFilter(getMaterialNames(), current, true);
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

      Material mat = Material.valueOf(argument.toUpperCase().replace(" ", "_").replace("-", "_"));
      FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Successfully generated $r{0}$b. ", mat.toString().toLowerCase().replace("_", " "));
      return new ItemStack(mat);
   }

   @NotNull
   @Override
   public ItemStack getDisplayStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isValid(argument, data, ffp)) {
         return ItemFactory.of(Material.BARRIER).name("§cInvalid Material §e" + argument).build();
      }

      Material mat = Material.valueOf(argument.toUpperCase().replace(" ", "_").replace("-", "_"));
      FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Successfully generated $r{0}$b. ", mat.toString().toLowerCase().replace("_", " "));
      return new ItemStack(mat);
   }

   @NotNull
   @Override
   public ArrayList<String> getDescription(@NotNull String argument, @NotNull String data) {
      if (!this.isValid(argument, data, null)) {
         return SilentNumbers.toArrayList("This material is $finvalid$b.");
      }

      Material mat = Material.valueOf(argument.toUpperCase().replace(" ", "_").replace("-", "_"));
      return SilentNumbers.toArrayList("This item must be a $r" + mat.toString().toLowerCase().replace("_", " ") + "$b.");
   }

   @Override
   public boolean determinateGeneration() {
      return true;
   }

   static ArrayList<String> getMaterialNames() {
      if (materialNames != null) {
         return materialNames;
      }

      materialNames = new ArrayList<>();

      for (Material m : Material.values()) {
         materialNames.add(m.toString());
      }

      return materialNames;
   }

   @NotNull
   @Override
   public String getSourcePlugin() {
      return "MythicLib";
   }

   @NotNull
   @Override
   public String getFilterName() {
      return "Vanilla";
   }

   @NotNull
   @Override
   public String exampleArgument() {
      return "carrot";
   }

   @NotNull
   @Override
   public String exampleData() {
      return "0";
   }

   public static void register() {
      global = new VanillaUIFilter();
      UIFilterManager.registerUIFilter(global);
   }

   @NotNull
   public static VanillaUIFilter get() {
      return global;
   }
}
