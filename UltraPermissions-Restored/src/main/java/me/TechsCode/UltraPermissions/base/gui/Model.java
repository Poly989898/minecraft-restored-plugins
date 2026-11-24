package me.TechsCode.UltraPermissions.base.gui;

import java.util.HashMap;

public class Model {
   private String title = "Unnamed Inventory";
   private int slots = 27;
   private final HashMap<Integer, Entry> entries = new HashMap<>();

   public Model() {
   }

   public void setTitle(String text) {
      this.title = text;
   }

   public void setSlots(int count) {
      this.slots = count;
   }

   public void button(Entry entry, int count) {
      this.entries.put(count, entry);
   }

   public void button(int count, Entry entry) {
      this.entries.put(count, entry);
   }

   public void button(Entry entry, int row, int column) {
      this.entries.put(this.format(row, column), entry);
   }

   public void button(int row, int column, Entry entry) {
      this.entries.put(this.format(row, column), entry);
   }

   public String getTitle() {
      return this.title;
   }

   public int getSlots() {
      return this.slots;
   }

   public HashMap<Integer, Entry> getEntries() {
      return this.entries;
   }

   private int format(int row, int column) {
      return (row - 1) * 9 + column;
   }
}
