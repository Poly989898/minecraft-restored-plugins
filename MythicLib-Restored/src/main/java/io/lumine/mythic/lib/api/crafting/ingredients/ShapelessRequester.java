package io.lumine.mythic.lib.api.crafting.ingredients;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShapelessRequester {
   final int requestIdentifier;
   boolean fulfilled = false;
   @NotNull
   final MythicIngredient ingredient;
   @NotNull
   final HashMap<Integer, ShapelessItem> dibbed = new HashMap<>();
   @NotNull
   final HashMap<Integer, ProvidedUIFilter> matched = new HashMap<>();
   @NotNull
   ArrayList<Integer> dibAmountOrder = new ArrayList<>();

   public ShapelessRequester(int id, @NotNull MythicIngredient ingredient) {
      this.requestIdentifier = id;
      this.ingredient = ingredient;
   }

   public int getRequestIdentifier() {
      return this.requestIdentifier;
   }

   public boolean isFulfilled() {
      return this.fulfilled;
   }

   @NotNull
   public MythicIngredient getIngredient() {
      return this.ingredient;
   }

   @NotNull
   public HashMap<Integer, ShapelessItem> getDibbed() {
      return this.dibbed;
   }

   @Nullable
   public ShapelessItem getDibbed(int id) {
      return this.dibbed.get(id);
   }

   @NotNull
   public HashMap<Integer, ProvidedUIFilter> getMatched() {
      return this.matched;
   }

   @Nullable
   public ProvidedUIFilter getMatched(int id) {
      return this.matched.get(id);
   }

   public void dib(@NotNull ShapelessItem item, @NotNull ProvidedUIFilter filter) {
      item.claimDibs(this);
      this.dibbed.put(item.getOperationalIdentifier(), item);
      this.matched.put(item.getOperationalIdentifier(), filter);
      boolean reg = false;

      for (int i = 0; i < this.dibAmountOrder.size(); i++) {
         Integer p = this.dibAmountOrder.get(i);
         ProvidedUIFilter dib = this.getMatched(p);
         if (dib != null && filter.getAmount(0) <= dib.getAmount(0)) {
            this.dibAmountOrder.add(i, item.getOperationalIdentifier());
            reg = true;
            break;
         }
      }

      if (!reg) {
         this.dibAmountOrder.add(item.getOperationalIdentifier());
      }
   }

   public boolean firstClaim() {
      for (Integer dib : this.dibAmountOrder) {
         ShapelessItem item = this.getDibbed(dib);
         ProvidedUIFilter filter = this.getMatched(dib);
         Validate.isTrue(filter != null && item != null, "Mismatch occurred between the order in which shapeless items are evaluated. ");
         if (item.getRequesters().size() == 1) {
            if (item.getAmount() < filter.getAmount(0)) {
               return false;
            }

            this.fulfilled = true;
            item.take(filter.getAmount(0));
            return true;
         }
      }

      for (Integer dib : this.dibAmountOrder) {
         ShapelessItem item = this.getDibbed(dib);
         ProvidedUIFilter filter = this.getMatched(dib);
         Validate.isTrue(filter != null && item != null, "Mismatch occurred between the order in which shapeless items are evaluated. ");
         if (item.getAmount() >= filter.getAmount(0)) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   public ArrayList<ArrayList<ShapelessItem>> secondClaim(@NotNull ArrayList<ShapelessItem> originalPool) {
      ArrayList<ShapelessItem> relevantPool = new ArrayList<>();

      for (ShapelessItem item : originalPool) {
         if (item.getRequesters().size() > 1) {
            relevantPool.add(item);
         } else if (item.getRequesters().size() == 0) {
            MythicLib.plugin
               .getLogger()
               .log(
                  Level.WARNING,
                  FriendlyFeedbackProvider.quickForConsole(
                     FFPMythicLib.get(),
                     "Undibbed item has bypassed onto the $rsecondClaim$b area: $u{0}$b. $fThis should be impossible, contact Gunging.",
                     SilentNumbers.getItemName(item.getStack())
                  )
               );
         }
      }

      ArrayList<ArrayList<ShapelessItem>> ret = new ArrayList<>();

      for (ShapelessItem path : this.getDibbed().values()) {
         ArrayList<ShapelessItem> pathPool = ShapelessItem.deepCloneList(relevantPool);
         boolean success = false;

         for (ShapelessItem observed : pathPool) {
            if (path.getOperationalIdentifier() == observed.getOperationalIdentifier()) {
               ProvidedUIFilter filter = this.getMatched(path.getOperationalIdentifier());
               Validate.isTrue(
                  filter != null,
                  "Fatal Error when evaluating Second Claim of Shapeless Recipe: Actually found matching ShapelessItem, but the filter was lost somehow."
               );
               if (observed.getAmount() >= filter.getAmount(0)) {
                  observed.take(filter.getAmount(0));
                  success = true;
                  break;
               }
            }
         }

         if (success) {
            ret.add(pathPool);
         }
      }

      return ret;
   }
}
