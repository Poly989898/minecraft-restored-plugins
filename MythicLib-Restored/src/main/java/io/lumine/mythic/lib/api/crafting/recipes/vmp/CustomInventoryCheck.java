package io.lumine.mythic.lib.api.crafting.recipes.vmp;

import io.lumine.mythic.lib.version.VInventoryView;
import org.jetbrains.annotations.NotNull;

public interface CustomInventoryCheck {
   boolean isTargetInventory(@NotNull VInventoryView var1);

   @NotNull
   String getCustomStationKey();
}
