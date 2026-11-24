package io.lumine.mythic.lib.api.crafting.recipes.vmp;

import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FurnaceMapping extends ThreeSlotMapping {
   @NotNull
   static final ArrayList<String> sNames = SilentNumbers.toArrayList("fuel");

   @NotNull
   @Override
   public ArrayList<String> getSideInventoryNames() {
      return sNames;
   }

   @NotNull
   @Override
   public InventoryType getIntendedInventory() {
      return InventoryType.FURNACE;
   }

   @Nullable
   @Override
   public MythicRecipeStation getIntendedStation() {
      return MythicRecipeStation.FURNACE;
   }
}
