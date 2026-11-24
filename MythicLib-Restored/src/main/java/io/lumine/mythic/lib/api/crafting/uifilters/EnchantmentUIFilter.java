package io.lumine.mythic.lib.api.crafting.uifilters;

import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnchantmentUIFilter implements UIFilter {
   static ArrayList<String> enchantmentNames = null;
   static EnchantmentUIFilter global;

   @NotNull
   @Override
   public String getIdentifier() {
      return "e";
   }

   @Override
   public boolean matches(@NotNull ItemStack item, @NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isValid(argument, data, ffp)) {
         return false;
      }

      if (this.cancelMatch(item, ffp)) {
         return false;
      }

      Enchantment wench = SilentNumbers.getEnchantment(argument);
      QuickNumberRange expectedLevel = QuickNumberRange.getFromString(data);
      ItemMeta iMeta = item.getItemMeta();
      if (iMeta.hasEnchant(wench)) {
         int currentLevel = iMeta.getEnchantLevel(wench);
         if (expectedLevel.inRange(currentLevel)) {
            FriendlyFeedbackProvider.log(
               ffp,
               FriendlyFeedbackCategory.SUCCESS,
               "This item had $r{0} {1}$b. $sPassed$b. ",
               wench.getKey().getKey().toLowerCase().replace("_", " "),
               SilentNumbers.toRomanNumerals(currentLevel)
            );
            return true;
         } else {
            FriendlyFeedbackProvider.log(
               ffp,
               FriendlyFeedbackCategory.FAILURE,
               "This had $r{0} {1}$b, expected $r{0} {2}$b. $fRejected$b. ",
               wench.getKey().getKey().toLowerCase().replace("_", " "),
               SilentNumbers.toRomanNumerals(currentLevel),
               this.getAsRomanNumeralRange(expectedLevel)
            );
            return false;
         }
      } else if (expectedLevel.inRange(0.0)) {
         FriendlyFeedbackProvider.log(
            ffp,
            FriendlyFeedbackCategory.SUCCESS,
            "This item didn't have the $r{0}$b enchantment. $sPassed$b. ",
            wench.getKey().getKey().toLowerCase().replace("_", " ")
         );
         return true;
      } else {
         FriendlyFeedbackProvider.log(
            ffp,
            FriendlyFeedbackCategory.FAILURE,
            "This item didn't have the $r{0}$b enchantment. $fRejected$b. ",
            wench.getKey().getKey().toLowerCase().replace("_", " ")
         );
         return false;
      }
   }

   @Override
   public boolean isValid(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      boolean failure = false;
      if (SilentNumbers.getEnchantment(argument) == null) {
         FriendlyFeedbackProvider.log(
            ffp, FriendlyFeedbackCategory.ERROR, "Expected an enchantment like $efire_protection$b or $esmite$b instead of $u{0}$b. ", argument
         );
         failure = true;
      }

      if (QuickNumberRange.getFromString(data, ffp) == null) {
         FriendlyFeedbackProvider.log(
            ffp, FriendlyFeedbackCategory.ERROR, "Enchantment level '$u{0}$b' is not an integer number or range (like $e3..4$b). ", data
         );
         failure = true;
      }

      return !failure;
   }

   @NotNull
   @Override
   public ArrayList<String> tabCompleteArgument(@NotNull String current) {
      return SilentNumbers.smartFilter(getEnchantmentNames(), current, true);
   }

   @NotNull
   @Override
   public ArrayList<String> tabCompleteData(@NotNull String argument, @NotNull String current) {
      return SilentNumbers.toArrayList("1", "2", "4", "6..", "..2", "3..5", "..");
   }

   @Override
   public boolean fullyDefinesItem() {
      return false;
   }

   @Nullable
   @Override
   public ItemStack getItemStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "Enchantment UIFilter $fcannot generate an item. ");
      return null;
   }

   @NotNull
   @Override
   public ItemStack getDisplayStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isValid(argument, data, ffp)) {
         return ItemFactory.of(Material.BARRIER).name("§cInvalid Enchantment §e" + argument + " " + data).build();
      }

      Enchantment wench = SilentNumbers.getEnchantment(argument);
      QuickNumberRange expectedLevel = QuickNumberRange.getFromString(data);
      return ItemFactory.of(Material.GOLDEN_APPLE)
         .name("§3Any Item")
         .enchant(wench, SilentNumbers.round(expectedLevel.getAsDouble(1.0)))
         .lore(this.getDescription(argument, data))
         .build();
   }

   @NotNull
   @Override
   public ArrayList<String> getDescription(@NotNull String argument, @NotNull String data) {
      return !this.isValid(argument, data, null)
         ? SilentNumbers.toArrayList("This item is $finvalid$b.")
         : SilentNumbers.toArrayList(
            "This item must be enchanted with $r"
               + SilentNumbers.getEnchantment(argument).getKey().getKey().toLowerCase().replace("_", " ")
               + " "
               + this.getAsRomanNumeralRange(QuickNumberRange.getFromString(data))
               + "$b."
         );
   }

   @NotNull
   public String getAsRomanNumeralRange(@NotNull QuickNumberRange qnr) {
      if (qnr.isSimple()) {
         return "$r" + SilentNumbers.toRomanNumerals(SilentNumbers.round(qnr.getMaximumInclusive()));
      }

      String minn = "-∞";
      String maxn = "∞";
      if (qnr.hasMin()) {
         minn = SilentNumbers.toRomanNumerals(SilentNumbers.round(qnr.getMinimumInclusive()));
      }

      if (qnr.hasMax()) {
         maxn = SilentNumbers.toRomanNumerals(SilentNumbers.round(qnr.getMaximumInclusive()));
      }

      return "$r" + minn + " $bthrough$r " + maxn;
   }

   static ArrayList<String> getEnchantmentNames() {
      if (enchantmentNames != null) {
         return enchantmentNames;
      }

      enchantmentNames = new ArrayList<>();

      for (Enchantment m : Enchantment.values()) {
         enchantmentNames.add(m.toString());
      }

      return enchantmentNames;
   }

   @NotNull
   @Override
   public String getSourcePlugin() {
      return "MythicLib";
   }

   @NotNull
   @Override
   public String getFilterName() {
      return "Enchantment";
   }

   @NotNull
   @Override
   public String exampleArgument() {
      return "smite";
   }

   @NotNull
   @Override
   public String exampleData() {
      return "2";
   }

   public static void register() {
      global = new EnchantmentUIFilter();
      UIFilterManager.registerUIFilter(global);
   }

   @NotNull
   public static EnchantmentUIFilter get() {
      return global;
   }
}
