package com.nexomc.libs.gui.components;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface GuiContainer {
   @NotNull Component title();

   void title(@NotNull Component var1);

   @NotNull Inventory createInventory(@NotNull InventoryHolder var1);

   @NotNull GuiType guiType();

   int inventorySize();

   int rows();

   public static class Chest implements GuiContainer {
      private final InventoryProvider.Chest inventoryProvider;
      private int rows;
      private Component title;

      public Chest(@NotNull Component title, InventoryProvider.@NotNull Chest inventoryProvider, int rows) {
         this.inventoryProvider = inventoryProvider;
         this.title = title;
         this.rows = rows;
      }

      public @NotNull Component title() {
         return this.title;
      }

      public void title(@NotNull Component title) {
         this.title = title;
      }

      public int inventorySize() {
         return this.rows * 9;
      }

      public @NotNull GuiType guiType() {
         return GuiType.CHEST;
      }

      public int rows() {
         return this.rows;
      }

      public void rows(int rows) {
         this.rows = rows;
      }

      public @NotNull Inventory createInventory(@NotNull InventoryHolder inventoryHolder) {
         return this.inventoryProvider.getInventory(this.title, inventoryHolder, this.inventorySize());
      }
   }

   public static class Typed implements GuiContainer {
      private final InventoryProvider.Typed inventoryProvider;
      private final GuiType guiType;
      private Component title;

      public Typed(@NotNull Component title, InventoryProvider.@NotNull Typed inventoryProvider, @NotNull GuiType guiType) {
         this.inventoryProvider = inventoryProvider;
         this.title = title;
         this.guiType = guiType;
      }

      public @NotNull Component title() {
         return this.title;
      }

      public void title(@NotNull Component title) {
         this.title = title;
      }

      public int inventorySize() {
         return this.guiType.getLimit();
      }

      public @NotNull GuiType guiType() {
         return this.guiType;
      }

      public int rows() {
         return 1;
      }

      public @NotNull Inventory createInventory(@NotNull InventoryHolder inventoryHolder) {
         return this.inventoryProvider.getInventory(this.title, inventoryHolder, this.guiType.getInventoryType());
      }
   }
}
