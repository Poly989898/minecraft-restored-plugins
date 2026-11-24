package io.lumine.mythic.lib.api.crafting.uifilters;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicItemUIFilter implements UIFilter {
   @NotNull
   public static final String MYTHIC_TYPE = "MYTHIC_TYPE";
   private static boolean reg = true;
   static MythicItemUIFilter global;

   @NotNull
   @Override
   public String getIdentifier() {
      return "mm";
   }

   @Override
   public boolean matches(@NotNull ItemStack item, @NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isValid(argument, data, ffp)) {
         return false;
      } else if (this.cancelMatch(item, ffp)) {
         return false;
      } else {
         NBTItem asNBT = NBTItem.get(item);
         String mythicType = asNBT.getString("MYTHIC_TYPE");
         if (argument.equals(mythicType)) {
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Item mythic type matched $s{0}$b. ", argument);
            return true;
         } else {
            FriendlyFeedbackProvider.log(
               ffp,
               FriendlyFeedbackCategory.FAILURE,
               "Item '$u{0}$b' of mythic type $r'{1}'$b does not match expected mythic type $f{2}$b. ",
               SilentNumbers.getItemName(item),
               mythicType,
               argument
            );
            return false;
         }
      }
   }

   @Override
   public boolean isValid(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (reg) {
         return true;
      } else {
         Optional<MythicItem> hasMythicItem = MythicBukkit.inst().getItemManager().getItem(argument);
         if (!hasMythicItem.isPresent()) {
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "No such mythic item named '$u{0}$b'. ", argument);
            return false;
         } else {
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "MythicItem found, $svalidated$b. ");
            return true;
         }
      }
   }

   @NotNull
   @Override
   public ArrayList<String> tabCompleteArgument(@NotNull String current) {
      return SilentNumbers.smartFilter(getMythicItemNames(), current, true);
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

      Optional<MythicItem> hasMythicItem = MythicBukkit.inst().getItemManager().getItem(argument);
      ItemStack item = ((BukkitItemStack)hasMythicItem.get().generateItemStack(1)).getItemStack();
      FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Successfully generated $r{0}$b. ", SilentNumbers.getItemName(item));
      return new ItemStack(item);
   }

   @NotNull
   @Override
   public ItemStack getDisplayStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
      if (!this.isValid(argument, data, ffp)) {
         return ItemFactory.of(Material.BARRIER).name("§cInvalid MythicItem §e" + argument).build();
      }

      Optional<MythicItem> hasMythicItem = MythicBukkit.inst().getItemManager().getItem(argument);
      ItemStack item = ((BukkitItemStack)hasMythicItem.get().generateItemStack(1)).getItemStack();
      FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Successfully generated $r{0}$b. ", SilentNumbers.getItemName(item));
      return new ItemStack(item);
   }

   @NotNull
   @Override
   public ArrayList<String> getDescription(@NotNull String argument, @NotNull String data) {
      if (!this.isValid(argument, data, null)) {
         return SilentNumbers.toArrayList("This mythic type is $finvalid$b.");
      }

      Optional<MythicItem> hasMythicItem = MythicBukkit.inst().getItemManager().getItem(argument);
      return SilentNumbers.toArrayList(
         "This item must be a $r" + SilentNumbers.getItemName(((BukkitItemStack)hasMythicItem.get().generateItemStack(1)).getItemStack()) + "$b."
      );
   }

   @Override
   public boolean determinateGeneration() {
      return true;
   }

   static ArrayList<String> getMythicItemNames() {
      return new ArrayList<>(MythicBukkit.inst().getItemManager().getItemNames());
   }

   @NotNull
   @Override
   public String getSourcePlugin() {
      return "MythicLib";
   }

   @NotNull
   @Override
   public String getFilterName() {
      return "MythicItem";
   }

   @NotNull
   @Override
   public String exampleArgument() {
      return "SKELETAL_CROWN";
   }

   @NotNull
   @Override
   public String exampleData() {
      return "0";
   }

   public static void register() {
      global = new MythicItemUIFilter();
      UIFilterManager.registerUIFilter(global);
      reg = false;
   }

   @NotNull
   public static MythicItemUIFilter get() {
      return global;
   }
}
