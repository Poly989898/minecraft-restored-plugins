package me.TechsCode.UltraPermissions.base.gui.craftingTable;

import java.util.HashMap;
import me.TechsCode.UltraPermissions.base.gui.Entry;

public class CraftingTableModel {
   private String title = "Unnamed CraftingTable";
   private int slots = 10;
   private final HashMap<Integer, Entry> entries = new HashMap<>();

   public CraftingTableModel() {
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

   public void button(Entry entry, int count, int column) {
      this.entries.put(this.format(count, column), entry);
   }

   public void button(int count, int column, Entry entry) {
      this.entries.put(this.format(count, column), entry);
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

   private int format(int count, int column) {
      return (count - 1) * 3 + column;
   }
}
