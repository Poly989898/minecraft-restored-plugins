package io.lumine.mythic.lib.api.crafting.recipes.vmp;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import java.util.ArrayList;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftingMapping extends VanillaInventoryMapping {
   @NotNull
   static final ArrayList<String> sNames = new ArrayList<>();

   @Override
   public int getMainWidth(int slot) throws IllegalArgumentException {
      switch (slot) {
         case 1:
         case 3:
            return 0;
         case 2:
         case 4:
            return 1;
         default:
            throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getMainHeight(int slot) throws IllegalArgumentException {
      switch (slot) {
         case 1:
         case 2:
            return 0;
         case 3:
         case 4:
            return -1;
         default:
            throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getMainSlot(int width, int height) throws IllegalArgumentException {
      switch (height) {
         case 0:
            switch (width) {
               case 0:
                  return 1;
               case 1:
                  return 2;
            }
         case -1:
            switch (width) {
               case 0:
                  return 3;
               case 1:
                  return 4;
            }
         default:
            throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getMainInventoryStart() {
      return 1;
   }

   @Override
   public int getMainInventoryWidth() {
      return 2;
   }

   @Override
   public int getMainInventoryHeight() {
      return 2;
   }

   @Override
   public int getResultWidth(int slot) throws IllegalArgumentException {
      if (slot == 0) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getResultHeight(int slot) throws IllegalArgumentException {
      if (slot == 0) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getResultSlot(int width, int height) throws IllegalArgumentException {
      if (width == 0 && height == 0) {
         return 0;
      } else {
         throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getResultInventoryStart() {
      return 0;
   }

   @Override
   public int getResultInventoryWidth() {
      return 1;
   }

   @Override
   public int getResultInventoryHeight() {
      return 1;
   }

   @Override
   public boolean isResultSlot(int slot) {
      return slot == 0;
   }

   @Override
   public int getSideWidth(@NotNull String side, int slot) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideHeight(@NotNull String side, int slot) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideSlot(@NotNull String side, int width, int height) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideInventoryStart(@NotNull String side) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideInventoryWidth(@NotNull String side) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideInventoryHeight(@NotNull String side) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public boolean applyToSideInventory(@NotNull Inventory inventory, @NotNull MythicRecipeInventory finalSide, @NotNull String sideKeyName, boolean amountOnly) {
      return false;
   }

   @Override
   public boolean mainIsResult() {
      return false;
   }

   @Nullable
   @Override
   public MythicRecipeStation getIntendedStation() {
      return MythicRecipeStation.WORKBENCH;
   }

   @NotNull
   @Override
   public InventoryType getIntendedInventory() {
      return InventoryType.CRAFTING;
   }

   @NotNull
   @Override
   public ArrayList<String> getSideInventoryNames() {
      return sNames;
   }
}
