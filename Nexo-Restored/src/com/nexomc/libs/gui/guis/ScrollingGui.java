package com.nexomc.libs.gui.guis;

import com.nexomc.libs.gui.components.GuiContainer;
import com.nexomc.libs.gui.components.InteractionModifier;
import com.nexomc.libs.gui.components.ScrollType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class ScrollingGui extends PaginatedGui {
   private final ScrollType scrollType;
   private int scrollSize = 0;

   public ScrollingGui(@NotNull GuiContainer guiContainer, int pageSize, @NotNull ScrollType scrollType, @NotNull Set<InteractionModifier> interactionModifiers) {
      super(guiContainer, pageSize, interactionModifiers);
      this.scrollType = scrollType;
   }

   public boolean next() {
      if (this.getPageNum() * this.scrollSize + this.getPageSize() >= this.getPageItems().size() + this.scrollSize) {
         return false;
      } else {
         this.setPageNum(this.getPageNum() + 1);
         this.updatePage();
         return true;
      }
   }

   public boolean previous() {
      if (this.getPageNum() - 1 == 0) {
         return false;
      } else {
         this.setPageNum(this.getPageNum() - 1);
         this.updatePage();
         return true;
      }
   }

   public void open(@NotNull HumanEntity player) {
      this.open(player, 1);
   }

   public void open(@NotNull HumanEntity player, int openPage) {
      if (!player.isSleeping()) {
         this.getInventory().clear();
         this.getMutableCurrentPageItems().clear();
         this.populateGui();
         if (this.getPageSize() == 0) {
            this.setPageSize(this.calculatePageSize());
         }

         if (this.scrollSize == 0) {
            this.scrollSize = this.calculateScrollSize();
         }

         if (openPage > 0 && openPage * this.scrollSize + this.getPageSize() <= this.getPageItems().size() + this.scrollSize) {
            this.setPageNum(openPage);
         }

         this.populatePage();
         player.openInventory(this.getInventory());
      }
   }

   void updatePage() {
      this.clearPage();
      this.populatePage();
   }

   private void populatePage() {
      for(GuiItem guiItem : this.getPage(this.getPageNum())) {
         if (this.scrollType == ScrollType.HORIZONTAL) {
            this.putItemHorizontally(guiItem);
         } else {
            this.putItemVertically(guiItem);
         }
      }

   }

   private int calculateScrollSize() {
      int counter = 0;
      if (this.scrollType == ScrollType.VERTICAL) {
         boolean foundCol = false;

         for(int row = 1; row <= this.getRows(); ++row) {
            for(int col = 1; col <= 9; ++col) {
               int slot = this.getSlotFromRowCol(row, col);
               if (this.getInventory().getItem(slot) == null) {
                  if (!foundCol) {
                     foundCol = true;
                  }

                  ++counter;
               }
            }

            if (foundCol) {
               return counter;
            }
         }

         return counter;
      } else {
         boolean foundRow = false;

         for(int col = 1; col <= 9; ++col) {
            for(int row = 1; row <= this.getRows(); ++row) {
               int slot = this.getSlotFromRowCol(row, col);
               if (this.getInventory().getItem(slot) == null) {
                  if (!foundRow) {
                     foundRow = true;
                  }

                  ++counter;
               }
            }

            if (foundRow) {
               return counter;
            }
         }

         return counter;
      }
   }

   private void putItemVertically(GuiItem guiItem) {
      for(int slot = 0; slot < this.getRows() * 9; ++slot) {
         if (this.getGuiItem(slot) == null && this.getInventory().getItem(slot) == null) {
            this.getMutableCurrentPageItems().put(slot, guiItem);
            this.getInventory().setItem(slot, guiItem.getItemStack());
            break;
         }
      }

   }

   private void putItemHorizontally(GuiItem guiItem) {
      for(int col = 1; col < 10; ++col) {
         for(int row = 1; row <= this.getRows(); ++row) {
            int slot = this.getSlotFromRowCol(row, col);
            if (this.getGuiItem(slot) == null && this.getInventory().getItem(slot) == null) {
               this.getMutableCurrentPageItems().put(slot, guiItem);
               this.getInventory().setItem(slot, guiItem.getItemStack());
               return;
            }
         }
      }

   }

   private List<GuiItem> getPage(int givenPage) {
      int page = givenPage - 1;
      int pageItemsSize = this.getPageItems().size();
      List<GuiItem> guiPage = new ArrayList();
      int max = page * this.scrollSize + this.getPageSize();
      if (max > pageItemsSize) {
         max = pageItemsSize;
      }

      for(int i = page * this.scrollSize; i < max; ++i) {
         guiPage.add((GuiItem)this.getPageItems().get(i));
      }

      return guiPage;
   }
}
