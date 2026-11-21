package com.nexomc.libs.gui.builder.gui;

import com.nexomc.libs.gui.components.GuiContainer;
import com.nexomc.libs.gui.components.InventoryProvider;
import com.nexomc.libs.gui.components.util.Legacy;
import com.nexomc.libs.gui.guis.BaseGui;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class BaseChestGuiBuilder<G extends BaseGui, B extends BaseChestGuiBuilder<G, B>> extends BaseGuiBuilder<G, B> {
   private int rows = 1;
   private InventoryProvider.Chest inventoryProvider = (title, owner, rows) -> Bukkit.createInventory(owner, rows, Legacy.SERIALIZER.serialize(title));

   @Contract("_ -> this")
   public @NotNull B rows(int rows) {
      this.rows = rows;
      return (B)this;
   }

   public B inventory(InventoryProvider.@NotNull Chest inventoryProvider) {
      this.inventoryProvider = inventoryProvider;
      return (B)this;
   }

   protected int getRows() {
      return this.rows;
   }

   protected InventoryProvider.@NotNull Chest getInventoryProvider() {
      return this.inventoryProvider;
   }

   protected GuiContainer.@NotNull Chest createContainer() {
      return new GuiContainer.Chest(this.getTitle(), this.inventoryProvider, this.getRows());
   }
}
