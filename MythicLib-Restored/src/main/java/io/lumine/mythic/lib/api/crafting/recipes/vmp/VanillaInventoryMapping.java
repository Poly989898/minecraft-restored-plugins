package io.lumine.mythic.lib.api.crafting.recipes.vmp;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.recipes.MythicCraftingManager;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import io.lumine.mythic.lib.version.VInventoryView;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class VanillaInventoryMapping {
   @NotNull
   static HashMap<InventoryType, VanillaInventoryMapping> vanillaMappings = new HashMap<>();
   @NotNull
   static ArrayList<VanillaInventoryMapping> customInventoryMappings = new ArrayList<>();

   public abstract int getMainWidth(int count) throws IllegalArgumentException;

   public abstract int getMainHeight(int count) throws IllegalArgumentException;

   public abstract int getMainSlot(int width, int height) throws IllegalArgumentException;

   public abstract int getMainInventoryStart();

   public int getMainInventorySize() {
      return this.getMainInventoryHeight() * this.getMainInventoryWidth();
   }

   public abstract int getMainInventoryWidth();

   public abstract int getMainInventoryHeight();

   @NotNull
   public MythicRecipeInventory getMainMythicInventory(@NotNull Inventory inventory) {
      MythicRecipeInventory main = new MythicRecipeInventory();

      for (int h = 0; h < this.getMainInventoryHeight(); h++) {
         ItemStack[] row = new ItemStack[this.getMainInventoryWidth()];

         for (int w = 0; w < this.getMainInventoryWidth(); w++) {
            row[w] = get(inventory, this.getMainSlot(w, -h));
         }

         main.addRow(row);
      }

      return main;
   }

   public abstract int getResultWidth(int count) throws IllegalArgumentException;

   public abstract int getResultHeight(int count) throws IllegalArgumentException;

   public abstract int getResultSlot(int width, int height) throws IllegalArgumentException;

   public int getResultInventorySize() {
      return this.getResultInventoryHeight() * this.getResultInventoryWidth();
   }

   public abstract int getResultInventoryStart();

   public abstract int getResultInventoryWidth();

   public abstract int getResultInventoryHeight();

   @NotNull
   public MythicRecipeInventory getResultMythicInventory(@NotNull Inventory inventory) {
      MythicRecipeInventory result = new MythicRecipeInventory();

      for (int h = 0; h < this.getResultInventoryHeight(); h++) {
         ItemStack[] row = new ItemStack[this.getResultInventoryWidth()];

         for (int w = 0; w < this.getResultInventoryWidth(); w++) {
            row[w] = get(inventory, this.getResultSlot(w, -h));
         }

         result.addRow(row);
      }

      return result;
   }

   public abstract int getSideWidth(@NotNull String text, int count) throws IllegalArgumentException;

   public abstract int getSideHeight(@NotNull String text, int count) throws IllegalArgumentException;

   public abstract int getSideSlot(@NotNull String side, int width, int height) throws IllegalArgumentException;

   public abstract int getSideInventoryStart(@NotNull String text) throws IllegalArgumentException;

   public int getSideInventorySize(@NotNull String side) throws IllegalArgumentException {
      return this.getSideInventoryHeight(side) * this.getSideInventoryWidth(side);
   }

   public abstract int getSideInventoryWidth(@NotNull String text) throws IllegalArgumentException;

   public abstract int getSideInventoryHeight(@NotNull String text) throws IllegalArgumentException;

   @NotNull
   public MythicRecipeInventory getSideMythicInventory(@NotNull String side, @NotNull Inventory inventory) throws IllegalArgumentException {
      this.validateSide(side);
      MythicRecipeInventory sideInven = new MythicRecipeInventory();

      for (int h = 0; h < this.getSideInventoryHeight(side); h++) {
         ItemStack[] row = new ItemStack[this.getSideInventoryWidth(side)];

         for (int w = 0; w < this.getSideInventoryWidth(side); w++) {
            row[w] = get(inventory, this.getSideSlot(side, w, -h));
         }

         sideInven.addRow(row);
      }

      return sideInven;
   }

   @NotNull
   public abstract ArrayList<String> getSideInventoryNames();

   @NotNull
   public HashMap<String, MythicRecipeInventory> getSideMythicInventories(@NotNull Inventory inventory) {
      HashMap<String, MythicRecipeInventory> sides = new HashMap<>();

      for (String side : this.getSideInventoryNames()) {
         sides.put(side, this.getSideMythicInventory(side, inventory));
      }

      return sides;
   }

   @Deprecated
   public void throwSideInventoryException(@NotNull String side) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @NotNull
   public IllegalArgumentException sideInventoryException(@NotNull String side) {
      return new IllegalArgumentException("No such side inventory of name '" + side + "' for mapping " + this.getClass().getSimpleName());
   }

   @Deprecated
   public void considerThrowingSideException(String side) {
      this.validateSide(side);
   }

   public void validateSide(@NotNull String side) {
      if (!this.getSideInventoryNames().contains(side)) {
         throw this.sideInventoryException(side);
      }
   }

   public void applyToMainInventory(@NotNull Inventory inventory, @NotNull MythicRecipeInventory finalMain, boolean amountOnly) {
      int z = 0;

      for (int s = this.getMainInventoryStart(); s < this.getMainInventorySize() + this.getMainInventoryStart() + z; s++) {
         int w = this.getMainWidth(s);
         int h = this.getMainHeight(s);
         if (w >= 0 && h <= 0) {
            setInventoryItem(inventory, s, finalMain.getItemAt(w, h), amountOnly);
         } else {
            z++;
         }
      }
   }

   public void applyToResultInventory(@NotNull Inventory inventory, @NotNull MythicRecipeInventory finalResult, boolean amountOnly) {
      int z = 0;

      for (int s = this.getResultInventoryStart(); s < this.getResultInventorySize() + this.getResultInventoryStart() + z; s++) {
         int w = this.getResultWidth(s);
         int h = this.getResultHeight(s);
         if (w >= 0 && h <= 0) {
            setInventoryItem(inventory, s, finalResult.getItemAt(this.getResultWidth(s), this.getResultHeight(s)), amountOnly);
         } else {
            z++;
         }
      }
   }

   public boolean applyToSideInventory(@NotNull Inventory inventory, @NotNull MythicRecipeInventory finalSide, @NotNull String sideKeyName, boolean amountOnly) {
      if (!this.getSideInventoryNames().contains(sideKeyName)) {
         return false;
      }

      for (int s = this.getSideInventoryStart(sideKeyName); s < this.getSideInventorySize(sideKeyName) + this.getSideInventoryStart(sideKeyName); s++) {
         setInventoryItem(inventory, s, finalSide.getItemAt(this.getSideWidth(sideKeyName, s), this.getSideHeight(sideKeyName, s)), amountOnly);
      }

      return true;
   }

   @NotNull
   public MythicBlueprintInventory extractFrom(@NotNull Inventory inven) {
      MythicRecipeInventory main = this.getMainMythicInventory(inven);
      MythicRecipeInventory result = this.getResultMythicInventory(inven);
      HashMap<String, MythicRecipeInventory> sideInventories = this.getSideMythicInventories(inven);
      MythicBlueprintInventory inventory = new MythicBlueprintInventory(main, result);

      for (String side : sideInventories.keySet()) {
         inventory.addSideInventory(side, sideInventories.get(side));
      }

      return inventory;
   }

   public abstract boolean isResultSlot(int count);

   public abstract boolean mainIsResult();

   @NotNull
   public abstract InventoryType getIntendedInventory();

   public static void setInventoryItemAmount(@Nullable Inventory inven, int slot, @Nullable ItemStack item) {
      if (inven != null) {
         ItemStack target = inven.getItem(slot);
         if (target != null) {
            int amount = 0;
            if (item != null) {
               amount = item.getAmount();
            }

            target.setAmount(amount);
         }
      }
   }

   public static void setInventoryItem(@Nullable Inventory inven, int slot, @Nullable ItemStack item) {
      if (inven != null) {
         inven.setItem(slot, item);
      }
   }

   public static void setInventoryItem(@Nullable Inventory inven, int slot, @Nullable ItemStack item, boolean asAmountInstead) {
      if (inven != null) {
         if (asAmountInstead) {
            setInventoryItemAmount(inven, slot, item);
         } else {
            setInventoryItem(inven, slot, item);
         }
      }
   }

   @NotNull
   public static ItemStack get(@NotNull Inventory inven, int slot) {
      if (inven.getSize() <= slot) {
         throw new IndexOutOfBoundsException("Attempted to get Non-Null item from a slot that does not exist.");
      }

      ItemStack ret = inven.getItem(slot);
      return ret == null ? MythicCraftingManager.AIR : ret;
   }

   @Deprecated
   public void throwOutOfBounds(int out) throws IllegalArgumentException {
      throw this.outOfBounds(out);
   }

   @NotNull
   public IllegalArgumentException outOfBounds(int out) {
      return new IllegalArgumentException("Mapping " + this.getClass().getSimpleName() + " has no data for slot '" + out + "'");
   }

   @Deprecated
   public void throwOutOfBounds(int w, int h) throws IllegalArgumentException {
      throw this.outOfBounds(w, h);
   }

   @NotNull
   public IllegalArgumentException outOfBounds(int width, int height) {
      return new IllegalArgumentException(
         "Mapping " + this.getClass().getSimpleName() + " has no data for slot at width '" + width + "' and height '" + height + "'"
      );
   }

   public static void registerAll() {
      vanillaMappings.clear();
      vanillaMappings.put(InventoryType.WORKBENCH, new WorkbenchMapping());
      vanillaMappings.put(InventoryType.CRAFTING, new CraftingMapping());
      vanillaMappings.put(InventoryType.FURNACE, new FurnaceMapping());
      registerCustomMapping(SuperWorkbenchMapping.SWB);
      registerCustomMapping(MegaWorkbenchMapping.MWB);
      if (MythicLib.plugin.getVersion().isAbove(1, 16)) {
         vanillaMappings.put(InventoryType.valueOf("SMITHING"), new LegacySmithingStationMapping());
      }
   }

   public static void registerCustomMapping(@NotNull VanillaInventoryMapping vmp) {
      if (vmp instanceof CustomInventoryCheck) {
         if (vmp.getIntendedInventory() == InventoryType.CHEST) {
            customInventoryMappings.add(vmp);
         }
      }
   }

   @Nullable
   public static VanillaInventoryMapping getMappingFor(@NotNull VInventoryView inven) {
      if (inven.getType() == InventoryType.CHEST && customInventoryMappings.size() > 0) {
         for (VanillaInventoryMapping vmp : customInventoryMappings) {
            if (((CustomInventoryCheck)vmp).isTargetInventory(inven)) {
               return vmp;
            }
         }

         return null;
      } else {
         return vanillaMappings.get(inven.getType());
      }
   }

   @Nullable
   public abstract MythicRecipeStation getIntendedStation();
}
