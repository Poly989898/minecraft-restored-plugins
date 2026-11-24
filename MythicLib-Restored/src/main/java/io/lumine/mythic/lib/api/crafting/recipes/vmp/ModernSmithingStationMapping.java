package io.lumine.mythic.lib.api.crafting.recipes.vmp;

import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class ModernSmithingStationMapping extends VanillaInventoryMapping {
   private static final int TEMPLATE_SLOT = 0;
   private static final int MAIN_SLOT = 1;
   private static final int INGOT_SLOT = 2;
   private static final int RESULT_SLOT = 3;
   @NotNull
   static final ArrayList<String> sNames = SilentNumbers.toArrayList("template", "ingot");

   @Override
   public int getMainWidth(int slot) throws IllegalArgumentException {
      if (slot == 1) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getMainHeight(int slot) throws IllegalArgumentException {
      if (slot == 1) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getMainSlot(int width, int height) throws IllegalArgumentException {
      if (height == 0 && width == 0) {
         return 1;
      } else {
         throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getMainInventoryStart() {
      return 1;
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
      if (slot == 3) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getResultHeight(int slot) throws IllegalArgumentException {
      if (slot == 3) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getResultSlot(int width, int height) throws IllegalArgumentException {
      if (width == 0 && height == 0) {
         return 3;
      } else {
         throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getResultInventoryStart() {
      return 3;
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
      return slot == 3;
   }

   @Override
   public int getSideWidth(@NotNull String side, int slot) throws IllegalArgumentException {
      this.validateSide(side);
      if (slot != 1 && slot != 2) {
         throw this.outOfBounds(slot);
      } else {
         return 0;
      }
   }

   @Override
   public int getSideHeight(@NotNull String side, int slot) throws IllegalArgumentException {
      this.validateSide(side);
      if (slot != 1 && slot != 2) {
         throw this.outOfBounds(slot);
      } else {
         return 0;
      }
   }

   @Override
   public int getSideSlot(@NotNull String side, int width, int height) throws IllegalArgumentException {
      this.validateSide(side);
      if (width == 0 && height == 0) {
         return side.equals("template") ? 0 : 2;
      } else {
         throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getSideInventoryStart(@NotNull String side) throws IllegalArgumentException {
      this.validateSide(side);
      return side.equals("template") ? 0 : 2;
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

   @NotNull
   @Override
   public ArrayList<String> getSideInventoryNames() {
      return sNames;
   }

   @NotNull
   @Override
   public InventoryType getIntendedInventory() {
      return InventoryType.SMITHING;
   }

   @Nullable
   @Override
   public MythicRecipeStation getIntendedStation() {
      return MythicRecipeStation.SMITHING;
   }
}
