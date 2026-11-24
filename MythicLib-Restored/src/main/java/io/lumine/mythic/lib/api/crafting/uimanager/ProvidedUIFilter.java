package io.lumine.mythic.lib.api.crafting.uimanager;

import io.lumine.mythic.lib.api.crafting.uifilters.UIFilter;
import io.lumine.mythic.lib.api.crafting.uifilters.VanillaUIFilter;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProvidedUIFilter implements Comparable<ProvidedUIFilter>, Cloneable {
   @NotNull
   UIFilter parent;
   String asString = null;
   @NotNull
   String argument;
   @NotNull
   String data;
   @Nullable
   QuickNumberRange amountRange;

   public ProvidedUIFilter(@NotNull UIFilter matchOperator, @NotNull String arg, @NotNull String dat) {
      this.parent = matchOperator;
      this.argument = arg;
      this.data = dat;
   }

   public ProvidedUIFilter(@NotNull UIFilter matchOperator, @NotNull String arg, @NotNull String dat, int amount) {
      this.parent = matchOperator;
      this.argument = arg;
      this.data = dat;
      this.setAmount(amount);
   }

   public ProvidedUIFilter(@NotNull UIFilter matchOperator, @NotNull String arg, @NotNull String dat, @Nullable QuickNumberRange amount) {
      this.parent = matchOperator;
      this.argument = arg;
      this.data = dat;
      this.setAmountRange(amount);
   }

   @Nullable
   public static ProvidedUIFilter getFromString(@Nullable String compound, @Nullable FriendlyFeedbackProvider ffp) {
      return UIFilterManager.getUIFilter(compound, ffp);
   }

   public boolean isAir() {
      return !(this.getParent() instanceof VanillaUIFilter) ? false : this.getArgument().equalsIgnoreCase("AIR");
   }

   public boolean matches(@NotNull ItemStack item, boolean ignoreAmount, @Nullable FriendlyFeedbackProvider ffp) {
      int amount = item.getAmount();
      if (!ignoreAmount && !this.checkAmount(amount)) {
         FriendlyFeedbackProvider.log(
            ffp,
            FriendlyFeedbackCategory.FAILURE,
            "The amount of this item ($u{0}$b) was $fnot the desired $b({1}$b). ",
            String.valueOf(amount),
            this.getAmountRange().toStringColored()
         );
         return false;
      } else {
         return this.getParent().matches(item, this.getArgument(), this.getData(), ffp);
      }
   }

   public boolean matches(@NotNull Inventory inv, int slot, int w, int h, boolean ignoreAmount, @Nullable FriendlyFeedbackProvider ffp) {
      QuickNumberRange amount = this.getAmountRange();
      if (ignoreAmount) {
         amount = null;
      }

      return this.getParent().matches(inv, slot, w, h, this.getArgument(), this.getData(), amount, ffp);
   }

   public boolean isValid(@Nullable FriendlyFeedbackProvider ffp) {
      return this.getParent().isValid(this.getArgument(), this.getData(), ffp);
   }

   @Nullable
   public ItemStack getItemStack(@Nullable FriendlyFeedbackProvider ffp) {
      ItemStack gen = this.getParent().getItemStack(this.getArgument(), this.getData(), ffp);
      if (gen == null) {
         return null;
      }

      gen.setAmount(this.getAmount(0));
      return gen;
   }

   @NotNull
   public ItemStack getDisplayStack(@Nullable FriendlyFeedbackProvider ffp) {
      ItemStack gen = this.getParent().getDisplayStack(this.getArgument(), this.getData(), ffp);
      gen.setAmount(this.getAmount(1));
      ItemMeta itemMeta = gen.getItemMeta();
      if (itemMeta != null) {
         if (gen.getAmount() > 1) {
            itemMeta.setDisplayName(SilentNumbers.getItemName(gen));
         } else {
            itemMeta.setDisplayName(SilentNumbers.getItemName(gen, false) + "§ʫ");
         }

         gen.setItemMeta(itemMeta);
      }

      return gen;
   }

   @NotNull
   public ArrayList<String> getDescription() {
      return this.getParent().getDescription(this.getArgument(), this.getData());
   }

   @NotNull
   public UIFilter getParent() {
      return this.parent;
   }

   public void setParent(@NotNull UIFilter filter) {
      this.parent = filter;
   }

   @Override
   public String toString() {
      if (this.asString != null) {
         return this.asString;
      }

      String amount = this.getAmountRange() == null ? "1.." : this.getAmountRange().toString();
      this.asString = this.getParent().getIdentifier() + " " + this.getArgument() + " " + this.getData() + " " + amount;
      return this.asString;
   }

   @NotNull
   public String getArgument() {
      return this.argument;
   }

   public void setArgument(@NotNull String arg) {
      this.argument = arg;
   }

   @NotNull
   public String getData() {
      return this.data;
   }

   public void setData(@NotNull String dat) {
      this.data = dat;
   }

   @Nullable
   public QuickNumberRange getAmountRange() {
      return this.amountRange;
   }

   public void setAmountRange(@Nullable QuickNumberRange range) {
      this.amountRange = range;
   }

   @Nullable
   public Integer getAmount() {
      if (this.getAmountRange() == null) {
         return null;
      } else {
         return !this.getAmountRange().isSimple() ? null : SilentNumbers.round(this.amountRange.getMinimumInclusive());
      }
   }

   public int getAmount(int def) {
      if (this.getAmountRange() == null) {
         return def;
      }

      if (!this.getAmountRange().isSimple()) {
         if (this.getAmountRange().hasMin()) {
            return SilentNumbers.round(this.getAmountRange().getMinimumInclusive());
         } else {
            return this.getAmountRange().hasMax() ? SilentNumbers.round(this.getAmountRange().getMaximumInclusive()) : def;
         }
      } else {
         return SilentNumbers.round(this.getAmountRange().getMinimumInclusive());
      }
   }

   public void setAmount(@Nullable Integer range) {
      if (range == null) {
         this.setAmountRange(null);
      } else {
         this.setAmountRange(new QuickNumberRange((double)range.intValue(), (double)range.intValue()));
      }
   }

   public boolean hasAmount() {
      return this.getAmountRange() != null;
   }

   public boolean checkAmount(double amount) {
      return this.isAir() ? true : checkAmount(this.getAmountRange(), amount);
   }

   public static boolean checkAmount(@Nullable QuickNumberRange range, double amount) {
      return range != null ? range.inRange(amount) : true;
   }

   public int compareTo(@NotNull ProvidedUIFilter comparate) {
      return this.getAmount(0) - comparate.getAmount(0);
   }

   public ProvidedUIFilter clone() {
      QuickNumberRange amr = this.getAmountRange() != null ? this.getAmountRange().clone() : null;
      return new ProvidedUIFilter(this.getParent(), this.getArgument(), this.getData(), amr);
   }
}
