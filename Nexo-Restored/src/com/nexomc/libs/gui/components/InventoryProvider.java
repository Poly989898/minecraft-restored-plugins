package com.nexomc.libs.gui.components;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public final class InventoryProvider {
   @FunctionalInterface
   public interface Chest {
      @NotNull Inventory getInventory(@NotNull Component var1, @NotNull InventoryHolder var2, int var3);
   }

   @FunctionalInterface
   public interface Typed {
      @NotNull Inventory getInventory(@NotNull Component var1, @NotNull InventoryHolder var2, @NotNull InventoryType var3);
   }
}
