package com.nexomc.libs.gui.guis;

import com.nexomc.libs.gui.components.GuiAction;
import com.nexomc.libs.gui.components.util.ItemNbt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class GuiListener implements Listener {
   @EventHandler
   public void onGuiClick(InventoryClickEvent event) {
      if (event.getInventory().getHolder() instanceof BaseGui) {
         BaseGui gui = (BaseGui)event.getInventory().getHolder();
         GuiAction<InventoryClickEvent> outsideClickAction = gui.getOutsideClickAction();
         if (outsideClickAction != null && event.getClickedInventory() == null) {
            outsideClickAction.execute(event);
         } else if (event.getClickedInventory() != null) {
            GuiAction<InventoryClickEvent> defaultTopClick = gui.getDefaultTopClickAction();
            if (defaultTopClick != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
               defaultTopClick.execute(event);
            }

            GuiAction<InventoryClickEvent> playerInventoryClick = gui.getPlayerInventoryAction();
            if (playerInventoryClick != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
               playerInventoryClick.execute(event);
            }

            GuiAction<InventoryClickEvent> defaultClick = gui.getDefaultClickAction();
            if (defaultClick != null) {
               defaultClick.execute(event);
            }

            GuiAction<InventoryClickEvent> slotAction = gui.getSlotAction(event.getSlot());
            if (slotAction != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
               slotAction.execute(event);
            }

            GuiItem guiItem;
            if (gui instanceof PaginatedGui) {
               PaginatedGui paginatedGui = (PaginatedGui)gui;
               guiItem = paginatedGui.getGuiItem(event.getSlot());
               if (guiItem == null) {
                  guiItem = paginatedGui.getPageItem(event.getSlot());
               }
            } else {
               guiItem = gui.getGuiItem(event.getSlot());
            }

            if (this.isGuiItem(event.getCurrentItem(), guiItem)) {
               GuiAction<InventoryClickEvent> itemAction = guiItem.getAction();
               if (itemAction != null) {
                  itemAction.execute(event);
               }

            }
         }
      }
   }

   @EventHandler
   public void onGuiDrag(InventoryDragEvent event) {
      if (event.getInventory().getHolder() instanceof BaseGui) {
         BaseGui gui = (BaseGui)event.getInventory().getHolder();
         GuiAction<InventoryDragEvent> dragAction = gui.getDragAction();
         if (dragAction != null) {
            dragAction.execute(event);
         }

      }
   }

   @EventHandler
   public void onGuiClose(InventoryCloseEvent event) {
      if (event.getInventory().getHolder() instanceof BaseGui) {
         BaseGui gui = (BaseGui)event.getInventory().getHolder();
         GuiAction<InventoryCloseEvent> closeAction = gui.getCloseGuiAction();
         if (closeAction != null && !gui.isUpdating() && gui.shouldRunCloseAction()) {
            closeAction.execute(event);
         }

      }
   }

   @EventHandler
   public void onGuiOpen(InventoryOpenEvent event) {
      if (event.getInventory().getHolder() instanceof BaseGui) {
         BaseGui gui = (BaseGui)event.getInventory().getHolder();
         GuiAction<InventoryOpenEvent> openAction = gui.getOpenGuiAction();
         if (openAction != null && !gui.isUpdating()) {
            openAction.execute(event);
         }

      }
   }

   private boolean isGuiItem(@Nullable ItemStack currentItem, @Nullable GuiItem guiItem) {
      if (currentItem != null && guiItem != null) {
         String nbt = ItemNbt.getString(currentItem, "mf-gui");
         return nbt == null ? false : nbt.equals(guiItem.getUuid().toString());
      } else {
         return false;
      }
   }
}
