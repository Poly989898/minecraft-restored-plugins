package io.lumine.mythic.lib.api.crafting.ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class MythicBlueprintInventory {
   @NotNull
   final MythicRecipeInventory mainInventory;
   @NotNull
   final MythicRecipeInventory resultInventory;
   @NotNull
   final HashMap<String, MythicRecipeInventory> sideInventories = new HashMap<>();

   @NotNull
   public MythicRecipeInventory getMainInventory() {
      return this.mainInventory;
   }

   @NotNull
   public MythicRecipeInventory getResultInventory() {
      return this.resultInventory;
   }

   @NotNull
   public MythicRecipeInventory getSideInventory(@NotNull String ofName) {
      Validate.isTrue(this.hasSideInventories(ofName), "You may not query for a side inventory that does not exist.");
      return this.sideInventories.get(ofName);
   }

   public boolean hasSideInventories(@NotNull String ofName) {
      return this.sideInventories.containsKey(ofName);
   }

   public MythicBlueprintInventory(@NotNull MythicRecipeInventory mainInventory, @NotNull MythicRecipeInventory resultInventory) {
      this.mainInventory = mainInventory;
      this.resultInventory = resultInventory;
   }

   @NotNull
   public ArrayList<String> getSideInventoryNames() {
      return new ArrayList<>(this.sideInventories.keySet());
   }

   public void addSideInventory(@NotNull String name, @NotNull MythicRecipeInventory inventory) {
      this.sideInventories.put(name, inventory);
   }

   public void clearSideInventories() {
      this.sideInventories.clear();
   }
}
