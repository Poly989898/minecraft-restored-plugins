package io.lumine.mythic.bukkit.utils.collections;

import java.util.List;

public class PageableList<T> {
   public static final int DEFAULT_PAGE_SIZE = 10;
   private static final int PAGE_WINDOW = 10;
   private List<T> list;
   private int pageSize = 10;
   private int page;
   private int startingIndex;
   private int endingIndex;
   private int maxPages;

   public PageableList(List<T> list) {
      this.list = list;
      this.page = 1;
      this.maxPages = 1;
      this.calculatePages();
   }

   private PageableList<T> calculatePages() {
      if (this.pageSize > 0) {
         if (this.list.size() % this.pageSize == 0) {
            this.maxPages = this.list.size() / this.pageSize;
         } else {
            this.maxPages = this.list.size() / this.pageSize + 1;
         }
      }

      return this;
   }

   public List<T> getList() {
      return this.list;
   }

   public List<T> getListForPage() {
      return this.list.subList(this.startingIndex, this.endingIndex);
   }

   public int getPageSize() {
      return this.pageSize;
   }

   public PageableList<T> setPageSize(int pageSize) {
      this.pageSize = pageSize;
      this.calculatePages();
      return this;
   }

   public int getPage() {
      return this.page;
   }

   public PageableList<T> setPage(int p) {
      if (p >= this.maxPages) {
         this.page = this.maxPages;
      } else if (p <= 1) {
         this.page = 1;
      } else {
         this.page = p;
      }

      this.startingIndex = this.pageSize * (this.page - 1);
      if (this.startingIndex < 0) {
         this.startingIndex = 0;
      }

      this.endingIndex = this.startingIndex + this.pageSize;
      if (this.endingIndex > this.list.size()) {
         this.endingIndex = this.list.size();
      }

      return this;
   }

   public int getMaxPages() {
      return this.maxPages;
   }

   public int getPreviousPage() {
      return this.page > 1 ? this.page - 1 : 0;
   }

   public int getNextPage() {
      return this.page < this.maxPages ? this.page + 1 : 0;
   }

   public int getMinPageRange() {
      return this.getPage() > 10 ? this.getPage() - 10 : 1;
   }

   public int getMaxPageRange() {
      return this.getPage() < this.getMaxPages() - 10 ? this.getPage() + 10 : this.getMaxPages();
   }
}
