package com.nexomc.libs.gui.guis;

import com.nexomc.libs.gui.components.GuiContainer;
import com.nexomc.libs.gui.components.InteractionModifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PaginatedGui extends BaseGui {
   private final List<GuiItem> pageItems = new ArrayList();
   private final Map<Integer, GuiItem> currentPage;
   private int pageSize;
   private int pageNum = 1;

   public PaginatedGui(@NotNull GuiContainer guiContainer, int pageSize, @NotNull Set<InteractionModifier> interactionModifiers) {
      super(guiContainer, interactionModifiers);
      this.pageSize = pageSize;
      this.currentPage = new LinkedHashMap(guiContainer.inventorySize());
   }

   public BaseGui setPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
   }

   public void addItem(@NotNull GuiItem item) {
      this.pageItems.add(item);
   }

   public void addItem(GuiItem... items) {
      this.pageItems.addAll(Arrays.asList(items));
   }

   public void update() {
      this.getInventory().clear();
      this.populateGui();
      this.updatePage();
   }

   public void updatePageItem(int slot, @NotNull ItemStack itemStack) {
      if (this.currentPage.containsKey(slot)) {
         GuiItem guiItem = (GuiItem)this.currentPage.get(slot);
         guiItem.setItemStack(itemStack);
         this.getInventory().setItem(slot, guiItem.getItemStack());
      }
   }

   public void updatePageItem(int row, int col, @NotNull ItemStack itemStack) {
      this.updateItem(this.getSlotFromRowCol(row, col), itemStack);
   }

   public void updatePageItem(int slot, @NotNull GuiItem item) {
      if (this.currentPage.containsKey(slot)) {
         GuiItem var10000 = (GuiItem)this.currentPage.get(slot);
         int index = this.pageItems.indexOf(this.currentPage.get(slot));
         this.currentPage.put(slot, item);
         this.pageItems.set(index, item);
         this.getInventory().setItem(slot, item.getItemStack());
      }
   }

   public void updatePageItem(int row, int col, @NotNull GuiItem item) {
      this.updateItem(this.getSlotFromRowCol(row, col), item);
   }

   public void removePageItem(@NotNull GuiItem item) {
      this.pageItems.remove(item);
      this.updatePage();
   }

   public void removePageItem(@NotNull ItemStack item) {
      Optional<GuiItem> guiItem = this.pageItems.stream().filter((it) -> it.getItemStack().equals(item)).findFirst();
      guiItem.ifPresent(this::removePageItem);
   }

   public void open(@NotNull HumanEntity player) {
      this.open(player, 1);
   }

   public void open(@NotNull HumanEntity player, int openPage) {
      if (!player.isSleeping()) {
         if (openPage <= this.getPagesNum() || openPage > 0) {
            this.pageNum = openPage;
         }

         this.getInventory().clear();
         this.currentPage.clear();
         this.populateGui();
         if (this.pageSize == 0) {
            this.pageSize = this.calculatePageSize();
         }

         this.populatePage();
         player.openInventory(this.getInventory());
      }
   }

   public @NotNull BaseGui updateTitle(@NotNull Component title) {
      this.setUpdating(true);
      List<HumanEntity> viewers = new ArrayList(this.getInventory().getViewers());
      GuiContainer guiContainer = this.guiContainer();
      guiContainer.title(title);
      this.setInventory(guiContainer.createInventory(this));

      for(HumanEntity player : viewers) {
         this.open(player, this.getPageNum());
      }

      this.setUpdating(false);
      return this;
   }

   public @NotNull Map<@NotNull Integer, @NotNull GuiItem> getCurrentPageItems() {
      return Collections.unmodifiableMap(this.currentPage);
   }

   public @NotNull List<@NotNull GuiItem> getPageItems() {
      return Collections.unmodifiableList(this.pageItems);
   }

   public int getCurrentPageNum() {
      return this.pageNum;
   }

   public int getNextPageNum() {
      return this.pageNum + 1 > this.getPagesNum() ? this.pageNum : this.pageNum + 1;
   }

   public int getPrevPageNum() {
      return this.pageNum - 1 == 0 ? this.pageNum : this.pageNum - 1;
   }

   public boolean next() {
      if (this.pageNum + 1 > this.getPagesNum()) {
         return false;
      } else {
         ++this.pageNum;
         this.updatePage();
         return true;
      }
   }

   public boolean previous() {
      if (this.pageNum - 1 == 0) {
         return false;
      } else {
         --this.pageNum;
         this.updatePage();
         return true;
      }
   }

   GuiItem getPageItem(int slot) {
      return (GuiItem)this.currentPage.get(slot);
   }

   private List<GuiItem> getPageNum(int givenPage) {
      int page = givenPage - 1;
      List<GuiItem> guiPage = new ArrayList();
      int max = page * this.pageSize + this.pageSize;
      if (max > this.pageItems.size()) {
         max = this.pageItems.size();
      }

      for(int i = page * this.pageSize; i < max; ++i) {
         guiPage.add((GuiItem)this.pageItems.get(i));
      }

      return guiPage;
   }

   public int getPagesNum() {
      if (this.pageSize == 0) {
         this.pageSize = this.calculatePageSize();
      }

      return (int)Math.ceil((double)this.pageItems.size() / (double)this.pageSize);
   }

   private void populatePage() {
      int slot = 0;
      int inventorySize = this.getInventory().getSize();
      Iterator<GuiItem> iterator = this.getPageNum(this.pageNum).iterator();

      while(iterator.hasNext() && slot < inventorySize) {
         if (this.getGuiItem(slot) == null && this.getInventory().getItem(slot) == null) {
            GuiItem guiItem = (GuiItem)iterator.next();
            this.currentPage.put(slot, guiItem);
            this.getInventory().setItem(slot, guiItem.getItemStack());
            ++slot;
         } else {
            ++slot;
         }
      }

   }

   Map<Integer, GuiItem> getMutableCurrentPageItems() {
      return this.currentPage;
   }

   void clearPage() {
      for(Map.Entry<Integer, GuiItem> entry : this.currentPage.entrySet()) {
         this.getInventory().setItem((Integer)entry.getKey(), (ItemStack)null);
      }

   }

   public void clearPageItems(boolean update) {
      this.pageItems.clear();
      if (update) {
         this.update();
      }

   }

   public void clearPageItems() {
      this.clearPageItems(false);
   }

   int getPageSize() {
      return this.pageSize;
   }

   int getPageNum() {
      return this.pageNum;
   }

   public void setPageNum(int pageNum) {
      this.pageNum = pageNum;
   }

   void updatePage() {
      this.clearPage();
      this.populatePage();
   }

   int calculatePageSize() {
      int counter = 0;

      for(int slot = 0; slot < this.getRows() * 9; ++slot) {
         if (this.getGuiItem(slot) == null) {
            ++counter;
         }
      }

      if (counter == 0) {
         return 1;
      } else {
         return counter;
      }
   }
}
