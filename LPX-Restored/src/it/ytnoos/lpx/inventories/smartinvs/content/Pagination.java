package it.ytnoos.lpx.inventories.smartinvs.content;

import it.ytnoos.lpx.inventories.smartinvs.ClickableItem;
import java.util.Arrays;

public interface Pagination {
   ClickableItem[] getPageItems();

   int getPage();

   Pagination page(int value);

   boolean isFirst();

   boolean isLast();

   Pagination first();

   Pagination previous();

   Pagination next();

   Pagination last();

   Pagination addToIterator(SlotIterator slotIterator);

   Pagination setItems(ClickableItem[] clickableItems);

   Pagination setItemsPerPage(int value);

   class Impl implements Pagination {
      private int page;
      private ClickableItem[] clickableItems = new ClickableItem[0];
      private int itemsPerPage = 5;

      public Impl() {
      }

      @Override
      public ClickableItem[] getPageItems() {
         return Arrays.copyOfRange(this.clickableItems, this.page * this.itemsPerPage, (this.page + 1) * this.itemsPerPage);
      }

      @Override
      public int getPage() {
         return this.page;
      }

      @Override
      public Pagination page(int value) {
         this.page = value;
         return this;
      }

      @Override
      public boolean isFirst() {
         return this.page == 0;
      }

      @Override
      public boolean isLast() {
         int value = (int)Math.ceil((double)this.clickableItems.length / this.itemsPerPage);
         return this.page >= value - 1;
      }

      @Override
      public Pagination first() {
         this.page = 0;
         return this;
      }

      @Override
      public Pagination previous() {
         if (!this.isFirst()) {
            this.page--;
         }

         return this;
      }

      @Override
      public Pagination next() {
         if (!this.isLast()) {
            this.page++;
         }

         return this;
      }

      @Override
      public Pagination last() {
         this.page = this.clickableItems.length / this.itemsPerPage;
         return this;
      }

      @Override
      public Pagination addToIterator(SlotIterator slotIterator) {
         for (ClickableItem clickableItem : this.getPageItems()) {
            slotIterator.next().set(clickableItem);
            if (slotIterator.ended()) {
               break;
            }
         }

         return this;
      }

      @Override
      public Pagination setItems(ClickableItem[] clickableItems2) {
         this.clickableItems = clickableItems2;
         return this;
      }

      @Override
      public Pagination setItemsPerPage(int value) {
         this.itemsPerPage = value;
         return this;
      }

   }
}
