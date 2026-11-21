package com.ticxo.modelengine.core.menu.widget;

import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TabWidget implements Widget {
   private final List<TabWidget.Tab> tabs = new ArrayList<>();
   @Getter
   private TabWidget.Tab selectedTab;

   @Override
   public ItemStack getItemForSlot(int size, int slot) {
      if (slot != 0 && slot < 8) {
         return this.tabs.size() < slot ? null : this.tabs.get(slot - 1).getItemStack();
      } else {
         return this.selectedTab == null ? null : this.selectedTab.getWidget().getItemForSlot(size, slot);
      }
   }

   @Override
   public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
      if (slot != 0 && slot < 8) {
         if (this.tabs.size() >= slot) {
            TabWidget.Tab t = this.tabs.get(slot - 1);
            if (this.selectedTab != t) {
               this.selectedTab = t;
               this.selectedTab.onSelect();
               screen.draw(true);
            }
         }
      } else {
         if (this.selectedTab != null) {
            this.selectedTab.getWidget().onClick(screen, player, slot, event);
         }
      }
   }

   public void addTab(TabWidget.Tab tab) {
      this.tabs.add(tab);
      if (this.selectedTab == null) {
         this.selectedTab = tab;
         this.selectedTab.onSelect();
      }
   }

   public void clearTab() {
      this.tabs.clear();
   }

   public interface Tab {
      ItemStack getItemStack();

      Widget getWidget();

      void onSelect();
   }
}
