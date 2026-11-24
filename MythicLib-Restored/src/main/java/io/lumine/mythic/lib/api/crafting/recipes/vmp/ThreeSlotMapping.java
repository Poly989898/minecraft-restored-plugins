package io.lumine.mythic.lib.api.crafting.recipes.vmp;

import org.jetbrains.annotations.NotNull;

public abstract class ThreeSlotMapping extends VanillaInventoryMapping {
   @Override
   public int getMainWidth(int slot) throws IllegalArgumentException {
      if (slot == 0) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getMainHeight(int slot) throws IllegalArgumentException {
      if (slot == 0) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getMainSlot(int width, int height) throws IllegalArgumentException {
      if (height == 0 && width == 0) {
         return 0;
      } else {
         throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getMainInventoryStart() {
      return 0;
   }

   @Override
   public int getMainInventoryWidth() {
      return 1;
   }

   @Override
   public int getMainInventoryHeight() {
      return 1;
   }

   @Override
   public int getResultWidth(int slot) throws IllegalArgumentException {
      if (slot == 2) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getResultHeight(int slot) throws IllegalArgumentException {
      if (slot == 2) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getResultSlot(int width, int height) throws IllegalArgumentException {
      if (width == 0 && height == 0) {
         return 2;
      } else {
         throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getResultInventoryStart() {
      return 2;
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
      return slot == 2;
   }

   @Override
   public int getSideWidth(@NotNull String side, int slot) throws IllegalArgumentException {
      this.validateSide(side);
      if (slot == 1) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getSideHeight(@NotNull String side, int slot) throws IllegalArgumentException {
      this.validateSide(side);
      if (slot == 1) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getSideSlot(@NotNull String side, int width, int height) throws IllegalArgumentException {
      this.validateSide(side);
      if (width == 0 && height == 0) {
         return 1;
      } else {
         throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getSideInventoryStart(@NotNull String side) throws IllegalArgumentException {
      this.validateSide(side);
      return 1;
   }

   @Override
   public int getSideInventoryWidth(@NotNull String side) throws IllegalArgumentException {
      this.validateSide(side);
      return 1;
   }

   @Override
   public int getSideInventoryHeight(@NotNull String side) throws IllegalArgumentException {
      this.validateSide(side);
      return 1;
   }

   @Override
   public boolean mainIsResult() {
      return false;
   }
}
