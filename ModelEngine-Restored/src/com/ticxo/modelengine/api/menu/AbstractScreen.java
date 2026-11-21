package com.ticxo.modelengine.api.menu;

import com.ticxo.modelengine.api.ModelEngineAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractScreen {
   @Getter
   protected final Player viewer;
   @Getter
   protected final Inventory inventory;
   protected final List<Widget> widgets = new ArrayList<>();
   protected final Map<Integer, Widget> widgetReference = new HashMap<>();
   protected final Set<Integer> dirtySlots = new HashSet<>();

   public AbstractScreen(Player viewer, String title, int rows) {
      this(viewer, Component.text(title), rows);
   }

   public AbstractScreen(Player viewer, Component component, int rows) {
      this.viewer = viewer;
      this.inventory = Bukkit.createInventory(null, rows * 9, component);
   }

   public void openScreen() {
      this.draw(true);
      this.viewer.openInventory(this.inventory);
      ModelEngineAPI.getAPI().getScreenManager().registerScreen(this);
   }

   public void onTick() {
      if (!this.dirtySlots.isEmpty()) {
         this.draw(false);
      }
   }

   public void onClick(Player player, int slot, InventoryClickEvent event) {
      Widget widget = this.widgetReference.get(slot);
      if (widget != null) {
         widget.onClick(this, player, slot, event);
      }
   }

   public void onClickPlayer(Player player, int slot, InventoryClickEvent event) {
      if (event.isShiftClick()) {
         event.setCancelled(true);
      }
   }

   public void addWidget(Widget widget) {
      this.widgets.add(widget);
   }

   public void markSlotDirty(int slot) {
      this.dirtySlots.add(slot);
   }

   public void markSlotsDirty(int... slots) {
      for (int slot : slots) {
         this.dirtySlots.add(slot);
      }
   }

   public void draw(boolean all) {
      int size = this.inventory.getSize();
      if (all) {
         this.inventory.clear();
         this.widgetReference.clear();

         for (int i = 0; i < size; i++) {
            for (int j = this.widgets.size() - 1; j >= 0; j--) {
               Widget widget = this.widgets.get(j);
               ItemStack item = widget.getItemForSlot(size, i);
               if (item != null) {
                  this.inventory.setItem(i, item);
                  this.widgetReference.put(i, widget);
                  break;
               }
            }
         }
      } else {
         if (this.dirtySlots.isEmpty()) {
            return;
         }

         outer:
         for (int slot : this.dirtySlots) {
            for (int j = this.widgets.size() - 1; j >= 0; j--) {
               Widget widget = this.widgets.get(j);
               ItemStack item = widget.getItemForSlot(size, slot);
               if (item != null) {
                  this.inventory.setItem(slot, item);
                  this.widgetReference.put(slot, widget);
                  continue outer;
               }
            }

            this.inventory.setItem(slot, null);
            this.widgetReference.remove(slot);
         }

         this.dirtySlots.clear();
      }
   }
}
