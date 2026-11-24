package io.lumine.mythic.lib.api.crafting.ingredients;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShapelessItem implements Cloneable {
   @NotNull
   ItemStack stack;
   int taken = 0;
   final int operationalIdentifier;
   final int originalWidthCoord;
   final int originalHeightCoord;
   @NotNull
   final HashMap<Integer, ShapelessRequester> unspecifiedAmountRequests = new HashMap<>();

   @NotNull
   public ItemStack getStack() {
      return this.stack;
   }

   public void take(int amount) {
      this.taken += amount;
      if (this.getTaken() > this.getOriginalAmount()) {
         MythicLib.plugin
            .getLogger()
            .log(
               Level.SEVERE,
               FriendlyFeedbackProvider.quickForConsole(
                  FFPMythicLib.get(),
                  "Error while evaluating a Shapeless Recipe: $fShapeless Item has overdue amount $r{0}$f out of $r{1}$f. ",
                  String.valueOf(this.getTaken()),
                  String.valueOf(this.getOriginalAmount())
               )
            );
      }
   }

   public int getTaken() {
      return this.taken;
   }

   public int getAmount() {
      return this.getStack().getAmount() - this.taken;
   }

   public int getOriginalAmount() {
      return this.getStack().getAmount();
   }

   public int getOperationalIdentifier() {
      return this.operationalIdentifier;
   }

   public int getOriginalWidthCoord() {
      return this.originalWidthCoord;
   }

   public int getOriginalHeightCoord() {
      return this.originalHeightCoord;
   }

   public ShapelessItem(int operationalID, @NotNull ItemStack shapelessItem, int originalW, int originalH) {
      Validate.isTrue(
         !SilentNumbers.isAir(shapelessItem),
         "Shapeless recipes do not support material " + shapelessItem.getType().toString() + " because it is not an item. "
      );
      this.stack = shapelessItem;
      this.operationalIdentifier = operationalID;
      this.originalWidthCoord = originalW;
      this.originalHeightCoord = originalH;
   }

   public void claimDibs(@NotNull ShapelessRequester me) {
      this.unspecifiedAmountRequests.put(me.getRequestIdentifier(), me);
   }

   @NotNull
   public HashMap<Integer, ShapelessRequester> getRequesters() {
      return this.unspecifiedAmountRequests;
   }

   public ShapelessItem clone() {
      try {
         super.clone();
      } catch (CloneNotSupportedException exception) {
      }

      ShapelessItem ret = new ShapelessItem(
         this.getOperationalIdentifier(), this.getStack().clone(), this.getOriginalWidthCoord(), this.getOriginalHeightCoord()
      );
      ret.take(this.getTaken());
      return ret;
   }

   public ShapelessItem completeClone() {
      ShapelessItem ret = this.clone();

      for (ShapelessRequester req : this.getRequesters().values()) {
         ret.claimDibs(req);
      }

      return ret;
   }

   @NotNull
   public static ArrayList<ShapelessItem> deepCloneList(@NotNull ArrayList<ShapelessItem> list) {
      ArrayList<ShapelessItem> ret = new ArrayList<>();

      for (ShapelessItem tm : list) {
         ret.add(tm.clone());
      }

      return ret;
   }

   @NotNull
   public static ArrayList<ShapelessItem> completeCloneList(@NotNull ArrayList<ShapelessItem> list) {
      ArrayList<ShapelessItem> ret = new ArrayList<>();

      for (ShapelessItem tm : list) {
         ret.add(tm.completeClone());
      }

      return ret;
   }
}
