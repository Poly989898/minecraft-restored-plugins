package io.lumine.mythic.lib.api.crafting.ingredients;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.recipes.MythicCraftingManager;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicRecipeInventory implements Cloneable {
   @NotNull
   private final HashMap<Integer, ArrayList<ItemStack>> contents = new HashMap<>();

   public int getHeight() {
      return this.contents.size();
   }

   public int getWidth() {
      return this.getHeight() == 0 ? 0 : this.contents.get(0).size();
   }

   @Nullable
   public ItemStack getItemAt(int width, int height) {
      height *= -1;
      if (height >= 0 && width >= 0 && height < this.getHeight() && width < this.getWidth()) {
         ArrayList<ItemStack> targetRow = this.contents.get(height);
         return targetRow.get(width);
      }

      if (height < 0) {
         MythicLib.plugin.getLogger().log(Level.WARNING, "Positive height provided, which is WRONG: " + height);
      }

      return null;
   }

   public boolean setItemAt(int width, int height, @Nullable ItemStack item) {
      height *= -1;
      if (height >= 0 && width >= 0 && height < this.getHeight() && width < this.getWidth()) {
         ArrayList<ItemStack> targetRow = this.contents.get(height);
         targetRow.remove(width);
         targetRow.add(width, item);
         return true;
      } else {
         return false;
      }
   }

   public void addRow(@Nullable ItemStack... contents) {
      this.setRow(this.getHeight(), contents);
   }

   public void setRow(int row, @Nullable ItemStack... contents) {
      Validate.isTrue(row >= 0, "Mythic Recipe Inventories cannot have a negative number of rows.");
      this.createRowsUntil(row);
      ArrayList<ItemStack> fill;
      if (contents == null) {
         fill = this.fill(new ArrayList<>());
      } else {
         fill = this.fill(SilentNumbers.toArrayList(contents));
      }

      this.contents.put(row, fill);
   }

   void createRowsUntil(int row) {
      Validate.isTrue(row >= 0, "Mythic Recipe Inventories cannot have a negative number of rows.");

      for (Integer i = 0; i <= row; i = i + 1) {
         if (this.contents.get(i) == null) {
            this.contents.put(i, this.fill(new ArrayList<>()));
         }
      }
   }

   ArrayList<ItemStack> fill(@NotNull ArrayList<ItemStack> list) {
      if (list.size() >= this.getWidth()) {
         this.updateRowSizesTo(list.size());
         return new ArrayList<>(list);
      }

      ArrayList<ItemStack> ret = new ArrayList<>();

      for (int j = 0; j < this.getWidth(); j++) {
         if (list.size() > j) {
            ret.add(list.get(j));
         } else {
            ret.add(null);
         }
      }

      return ret;
   }

   void updateRowSizesTo(int size) {
      if (this.getHeight() != 0) {
         if (size != this.getWidth()) {
            ArrayList<ItemStack> first = this.contents.get(0);

            for (int j = 0; j < size; j++) {
               if (first.size() == j) {
                  first.add(null);
               }
            }

            this.contents.put(0, first);

            for (int l = 1; l < this.getHeight(); l++) {
               ArrayList<ItemStack> lst = this.contents.get(l);
               this.contents.put(l, this.fill(lst));
            }
         }
      }
   }

   public void clearContents() {
      this.contents.clear();
   }

   public void clean() {
      for (int w = 0; w < this.getWidth(); w++) {
         for (int h = 0; h < this.getHeight(); h++) {
            if (this.getItemAt(w, -h) != null) {
               this.setItemAt(w, -h, MythicCraftingManager.AIR);
            }
         }
      }
   }

   public MythicRecipeInventory clone() {
      try {
         super.clone();
      } catch (CloneNotSupportedException var7) {
      }

      MythicRecipeInventory ret = new MythicRecipeInventory();

      for (Entry<Integer, ArrayList<ItemStack>> row : this.contents.entrySet()) {
         ItemStack[] newRow = new ItemStack[this.getWidth()];

         for (int w = 0; w < this.getWidth(); w++) {
            ItemStack stacc = row.getValue().get(w);
            if (stacc != null) {
               newRow[w] = stacc.clone();
            } else {
               newRow[w] = null;
            }
         }

         ret.setRow(row.getKey(), newRow);
      }

      return ret;
   }

   @Nullable
   public ItemStack getFirst() {
      for (ArrayList<ItemStack> cont : this.contents.values()) {
         for (ItemStack itm : cont) {
            if (!SilentNumbers.isAir(itm)) {
               return itm;
            }
         }
      }

      return null;
   }

   @NotNull
   public ArrayList<String> toStrings(@NotNull String prefix) {
      ArrayList<String> ret = new ArrayList<>();

      for (int r = 0; r < this.contents.size(); r++) {
         ArrayList<ItemStack> fill = this.contents.get(r);
         if (fill != null) {
            ret.add(prefix + SilentNumbers.collapseList(SilentNumbers.transcribeList(fill, o -> SilentNumbers.getItemName((ItemStack)o)), "§7,§f "));
         }
      }

      return ret;
   }
}
