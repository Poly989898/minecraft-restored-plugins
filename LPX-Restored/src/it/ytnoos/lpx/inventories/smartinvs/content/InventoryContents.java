package it.ytnoos.lpx.inventories.smartinvs.content;

import it.ytnoos.lpx.IterationType;
import it.ytnoos.lpx.inventories.smartinvs.ClickableItem;
import it.ytnoos.lpx.inventories.smartinvs.SmartInventory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface InventoryContents {
   SmartInventory getSmartInventory();

   Pagination getPagination();

   Optional iterator(Object object);

   SlotIterator newIterator(Object object, IterationType wz, int value, int value2);

   SlotIterator newIterator(IterationType wz, int value, int value2);

   SlotIterator newIterator(String string, IterationType wz, SlotPos slotPos);

   SlotIterator newIterator(IterationType wz, SlotPos slotPos);

   ClickableItem[][] getClickableItems();

   Optional firstEmpty(long longValue);

   Optional<ClickableItem> get(int value, long longValue, int value2);

   Optional get(int value, int value2, SlotPos slotPos, int value3);

   InventoryContents set(int value, int value2, ClickableItem clickableItem);

   InventoryContents set(SlotPos slotPos, ClickableItem clickableItem);

   InventoryContents add(ClickableItem clickableItem, long longValue);

   InventoryContents fill(ClickableItem clickableItem, long longValue);

   InventoryContents fillRow(int value, long longValue, ClickableItem clickableItem);

   InventoryContents fillColumn(int value, ClickableItem clickableItem, long longValue);

   InventoryContents fillBorders(long longValue, ClickableItem clickableItem);

   InventoryContents fillRect(int value, long longValue, int value2, int value3, int value4, ClickableItem clickableItem);

   InventoryContents fillRect(SlotPos slotPos, SlotPos slotPos2, long longValue, ClickableItem clickableItem);

   Object property(Object object);

   Object property(long longValue, String string, Object object);

   InventoryContents setProperty(Object object, Object object2);

   class Impl implements InventoryContents {
      private final SmartInventory smartInventory;
      private final Player player;
      private final ClickableItem[][] clickableItems;
      private final Pagination pagination = new Pagination.Impl();
      private final Map<String, SlotIterator> iterators = new HashMap<>();
      private final Map<String, Object> properties = new HashMap<>();

      public Impl(SmartInventory smartInventory2, Player player2) {
         this.smartInventory = smartInventory2;
         this.player = player2;
         this.clickableItems = new ClickableItem[smartInventory2.getRows()][smartInventory2.getColumns()];
      }

      @Override
      public SmartInventory getSmartInventory() {
         return this.smartInventory;
      }

      @Override
      public Pagination getPagination() {
         return this.pagination;
      }

      @Override
      public Optional iterator(Object object) {
         return Optional.ofNullable(this.iterators.get(object));
      }

      @Override
      public SlotIterator newIterator(Object object, IterationType wz, int value, int value2) {
         SlotIterator.Impl impl = new SlotIterator.Impl(this, this.smartInventory, wz, value, value2);
         this.iterators.put((String)object, impl);
         return impl;
      }

      @Override
      public SlotIterator newIterator(String string, IterationType wz, SlotPos slotPos) {
         return this.newIterator(string, wz, slotPos.getRow(), slotPos.getColumn());
      }

      @Override
      public SlotIterator newIterator(IterationType wz, int value, int value2) {
         return new SlotIterator.Impl(this, this.smartInventory, wz, value, value2);
      }

      @Override
      public SlotIterator newIterator(IterationType wz, SlotPos slotPos) {
         return this.newIterator(wz, slotPos.getRow(), slotPos.getColumn());
      }

      @Override
      public ClickableItem[][] getClickableItems() {
         return this.clickableItems;
      }

      @Override
      public Optional firstEmpty(long longValue) {
         long longValue2 = longValue ^ 136037882753344L;
         IterationType.getPacketWrappers();
         int value = 0;

         while (value < this.clickableItems.length) {
            int value2 = 0;

            while (true) {
               if (value2 >= this.clickableItems[0].length) {
                  value++;
                  if (0L <= longValue) {
                     break;
                  }
               }

               Optional optional = this.get(value, longValue2, value2);
               if (-7210432490371770710L < longValue) {
                  ;
               }

               if (!optional.isPresent()) {
                  return Optional.of(new SlotPos(value, value2));
               }

               value2++;
            }
         }

         return Optional.empty();
      }

      @Override
      public Optional<ClickableItem> get(int value, long longValue, int value2) {
         int value3 = value;
         InventoryContents.Impl impl = this;
         if (longValue >= 1L) {
            if (value >= this.clickableItems.length) {
               return Optional.empty();
            }

            value3 = value2;
            impl = this;
         }

         return value3 >= impl.clickableItems[value].length ? Optional.empty() : Optional.ofNullable(this.clickableItems[value][value2]);
      }

      @Override
      public Optional get(int value, int value2, SlotPos slotPos, int value3) {
         long longValue = (long)value << 32 | (long)value2 << 56 >>> 32 | (long)value3 << 40 >>> 40;
         long longValue2 = longValue ^ 37597473432628L;
         return this.get(slotPos.getRow(), longValue2, slotPos.getColumn());
      }

      @Override
      public InventoryContents set(int value, int value2, ClickableItem clickableItem) {
         if (value >= this.clickableItems.length) {
            return this;
         }

         if (value2 >= this.clickableItems[value].length) {
            return this;
         }

         this.clickableItems[value][value2] = clickableItem;
         this.update(value, value2, clickableItem != null ? clickableItem.getItem() : null);
         return this;
      }

      @Override
      public InventoryContents set(SlotPos slotPos, ClickableItem clickableItem) {
         return this.set(slotPos.getRow(), slotPos.getColumn(), clickableItem);
      }

      @Override
      public InventoryContents add(ClickableItem clickableItem, long longValue) {
         IterationType.getPacketWrappers();
         int value = 0;

         label34:
         while (value < this.clickableItems.length) {
            for (int value2 = 0; value2 < this.clickableItems[0].length || longValue < 1L; value2++) {
               if (longValue < 0L) {
                  continue label34;
               }

               if (this.clickableItems[value][value2] == null) {
                  this.set(value, value2, clickableItem);
                  return this;
               }
            }

            value++;
         }

         return this;
      }

      @Override
      public InventoryContents fill(ClickableItem clickableItem, long longValue) {
         IterationType.getPacketWrappers();
         int value = 0;

         label23:
         while (value < this.clickableItems.length) {
            for (int value2 = 0; value2 < this.clickableItems[value].length; value2++) {
               this.set(value, value2, clickableItem);
               if (1L >= longValue) {
                  continue label23;
               }
            }

            value++;
         }

         return this;
      }

      @Override
      public InventoryContents fillRow(int value, long longValue, ClickableItem clickableItem) {
         if (value >= this.clickableItems.length) {
            return this;
         }

         for (int value2 = 0; value2 < this.clickableItems[value].length; value2++) {
            if (longValue > 1L) {
               this.set(value, value2, clickableItem);
            }
         }

         return this;
      }

      @Override
      public InventoryContents fillColumn(int value, ClickableItem clickableItem, long longValue) {
         for (int value2 = 0; value2 < this.clickableItems.length; value2++) {
            if (0L <= longValue) {
               this.set(value2, value, clickableItem);
            }
         }

         return this;
      }

      @Override
      public InventoryContents fillBorders(long longValue, ClickableItem clickableItem) {
         long longValue2 = longValue ^ 130096183645538L;
         this.fillRect(0, longValue2, 0, this.smartInventory.getRows() - 1, this.smartInventory.getColumns() - 1, clickableItem);
         return this;
      }

      @Override
      public InventoryContents fillRect(int value, long longValue, int value2, int value3, int value4, ClickableItem clickableItem) {
         IterationType.getPacketWrappers();
         int value5 = value;

         label65:
         while (value5 <= value3) {
            int value6 = value2;

            while (value6 <= value4 || longValue < 1L) {
               if (-7876305164402785966L >= longValue) {
                  continue label65;
               }

               label54: {
                  label69: {
                     int value7 = value5;
                     if (0L <= longValue) {
                        if (value5 == value) {
                           break label69;
                        }

                        value7 = value5;
                     }

                     if (longValue > -3420244829004031404L) {
                        if (value7 == value3) {
                           break label69;
                        }

                        value7 = value6;
                     }

                     if (value7 != value2 && value6 != value4) {
                        if (longValue < 1L) {
                           continue;
                        }
                        break label54;
                     }
                  }

                  this.set(value5, value6, clickableItem);
               }

               value6++;
            }

            value5++;
         }

         return this;
      }

      @Override
      public InventoryContents fillRect(SlotPos slotPos, SlotPos slotPos2, long longValue, ClickableItem clickableItem) {
         long longValue2 = longValue ^ 11959493428169L;
         return this.fillRect(slotPos.getRow(), longValue2, slotPos.getColumn(), slotPos2.getRow(), slotPos2.getColumn(), clickableItem);
      }

      @Override
      public Object property(Object object) {
         return this.properties.get(object);
      }

      @Override
      public Object property(long longValue, String string, Object object) {
         return this.properties.containsKey(string) ? this.properties.get(string) : object;
      }

      @Override
      public InventoryContents setProperty(Object object, Object object2) {
         this.properties.put((String)object, object2);
         return this;
      }

      private void update(int value, int value2, ItemStack itemStack) {
         if (this.smartInventory.getInventoryManager().getOpenedPlayers(this.smartInventory).contains(this.player)) {
            Inventory inventory = this.player.getOpenInventory().getTopInventory();
            inventory.setItem(this.smartInventory.getColumns() * value + value2, itemStack);
         }
      }

   }
}
