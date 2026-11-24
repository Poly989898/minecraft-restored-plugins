package io.lumine.mythic.lib.api.crafting.uifilters;

import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterCountermatch;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UIFilter {
   @NotNull
   HashMap<String, ArrayList<UIFilterCountermatch>> matchOverrides = new HashMap<>();

   @NotNull
   String getIdentifier();

   default boolean matches(
      @NotNull Inventory inventory,
      int targetSlot,
      int width,
      int height,
      @NotNull String argument,
      @NotNull String data,
      @Nullable QuickNumberRange amount,
      @Nullable FriendlyFeedbackProvider ffp
   ) {
      ItemStack item = inventory.getItem(targetSlot);
      if (item == null) {
         item = new ItemStack(Material.AIR);
      }

      int itemAmount = item.getAmount();
      if (!ProvidedUIFilter.checkAmount(amount, itemAmount)) {
         FriendlyFeedbackProvider.log(
            ffp,
            FriendlyFeedbackCategory.FAILURE,
            "The amount of this item ($u{0}$b) was $fnot the desired $b({1}$b). ",
            String.valueOf(itemAmount),
            amount.toStringColored()
         );
         return false;
      } else {
         return this.matches(item, argument, data, ffp);
      }
   }

   boolean matches(@NotNull ItemStack var1, @NotNull String var2, @NotNull String var3, @Nullable FriendlyFeedbackProvider var4);

   boolean isValid(@NotNull String var1, @NotNull String var2, @Nullable FriendlyFeedbackProvider var3);

   @NotNull
   ArrayList<String> tabCompleteArgument(@NotNull String var1);

   @NotNull
   ArrayList<String> tabCompleteData(@NotNull String var1, @NotNull String var2);

   boolean fullyDefinesItem();

   default boolean useInventoryMatch() {
      return false;
   }

   @Nullable
   ItemStack getItemStack(@NotNull String var1, @NotNull String var2, @Nullable FriendlyFeedbackProvider var3);

   @NotNull
   ItemStack getDisplayStack(@NotNull String var1, @NotNull String var2, @Nullable FriendlyFeedbackProvider var3);

   @NotNull
   ArrayList<String> getDescription(@NotNull String var1, @NotNull String var2);

   default boolean determinateGeneration() {
      return true;
   }

   default boolean partialDeterminateGeneration(@NotNull String argument, @NotNull String data) {
      return this.determinateGeneration();
   }

   default void addMatchOverride(@NotNull UIFilterCountermatch countermatch) {
      matchOverrides.put(this.getIdentifier(), SilentNumbers.addAll(matchOverrides.get(this.getIdentifier()), countermatch));
   }

   default boolean cancelMatch(@NotNull ItemStack item, @Nullable FriendlyFeedbackProvider ffp) {
      matchOverrides.computeIfAbsent(this.getIdentifier(), k -> new ArrayList<>());
      boolean failure = false;

      for (UIFilterCountermatch ufc : matchOverrides.get(this.getIdentifier())) {
         int e = 0;
         if (ffp != null) {
            ffp.messagesTotal();
         }

         boolean thisPrevent = ufc.preventsMatching(item, ffp);
         failure = thisPrevent || failure;
         if (thisPrevent && ffp != null && ffp.messagesTotal() == e) {
            ffp.log(
               FriendlyFeedbackCategory.ERROR,
               "Item match was cancelled by $f{0}$b for an unspecified reason.",
               ufc.getClass().getPackage().getName() + "." + ufc.getClass().getSimpleName()
            );
         }
      }

      return failure;
   }

   @NotNull
   String getSourcePlugin();

   @NotNull
   String getFilterName();

   @NotNull
   String exampleArgument();

   @NotNull
   String exampleData();
}
