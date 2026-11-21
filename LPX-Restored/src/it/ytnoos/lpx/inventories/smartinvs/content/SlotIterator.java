package it.ytnoos.lpx.inventories.smartinvs.content;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.google.common.collect.Sets;
import it.ytnoos.lpx.ClassKeyDerivation;
import it.ytnoos.lpx.IterationType;
import it.ytnoos.lpx.inventories.smartinvs.ClickableItem;
import it.ytnoos.lpx.inventories.smartinvs.SmartInventory;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.Set;

public interface SlotIterator {
   Optional get(long longValue);

   SlotIterator set(ClickableItem clickableItem);

   SlotIterator previous(long longValue);

   SlotIterator next();

   SlotIterator blacklist(int value, int value2);

   SlotIterator blacklist(SlotPos slotPos);

   int row();

   SlotIterator row(int value);

   int column();

   SlotIterator column(int value);

   boolean started();

   boolean ended();

   boolean doesAllowOverride();

   SlotIterator allowOverride(boolean flag);

   class Impl implements SlotIterator {
      private final InventoryContents inventoryContents;
      private final SmartInventory smartInventory;
      private final IterationType iterationType;
      private final Set<SlotPos> blacklisted = Sets.newHashSet();
      private boolean started = false;
      private boolean allowOverride = true;
      private int row;
      private int column;
      private static final long a = ClassKeyDerivation.a(-8514777243312620562L, 7495656129711808872L, MethodHandles.lookup().lookupClass()).a(115693522996610L);

      public Impl(InventoryContents inventoryContents2, SmartInventory smartInventory2, IterationType wz, int value, int value2) {
         this.inventoryContents = inventoryContents2;
         this.smartInventory = smartInventory2;
         this.iterationType = wz;
         this.row = value;
         this.column = value2;
      }

      public Impl(InventoryContents inventoryContents2, SmartInventory smartInventory2, IterationType wz) {
         this(inventoryContents2, smartInventory2, wz, 0, 0);
      }

      @Override
      public Optional get(long longValue) {
         long longValue2 = longValue ^ 32238166443332L;
         return this.inventoryContents.get(this.row, longValue2, this.column);
      }

      @Override
      public SlotIterator set(ClickableItem clickableItem) {
         long longValue = 10955514806272L;
         long longValue2 = longValue ^ 111161690257178L;
         if (this.fn(new Object[]{longValue2})) {
            this.inventoryContents.set(this.row, this.column, clickableItem);
         }

         return this;
      }

      // $VF: Irreducible bytecode was duplicated to produce valid code
      @Override
      public SlotIterator previous(long longValue) {
         long longValue2 = longValue ^ 91210411773655L;
         PacketWrapper[] packetWrappers = IterationType.getPacketWrappers();
         SlotIterator.Impl impl;
         if (this.row == 0) {
            impl = this;
            if (1L <= longValue) {
               if (this.column == 0) {
                  this.started = true;
                  return this;
               }

               impl = this;
            }
         } else {
            impl = this;
         }

         label85:
         while (true) {
            label90: {
               if (!impl.started) {
                  this.started = true;
                  Object[] objects = packetWrappers;
                  if (-1991644226447607469L <= longValue) {
                     Object[] objects2 = new Object[1];
                     objects = objects2;
                     objects2[0] = longValue2;
                  }

                  if (!this.fn(objects)) {
                     if (this.row != 0) {
                        impl = this;
                        continue;
                     }

                     if (this.column != 0) {
                        break label90;
                     }
                  }

                  if (1L < longValue) {
                     return this;
                  }
               }

               do {
                  impl = switch (this.iterationType) {
                     case HORIZONTAL -> {
                        this.column--;
                        if (this.column == 0) {
                           this.column = this.smartInventory.getColumns() - 1;
                           this.row--;
                        }

                        yield this;
                     }
                     case VERTICAL -> {
                        this.row--;
                        if (this.row == 0) {
                           this.row = this.smartInventory.getRows() - 1;
                           this.column--;
                        }
                        yield this;
                     }
                     default -> this;
                  };
                  Object[] objects3 = packetWrappers;
                  if (-1991644226447607469L <= longValue) {
                     Object[] objects4 = new Object[1];
                     objects3 = objects4;
                     objects4[0] = longValue2;
                  }

                  if (!impl.fn(objects3)) {
                     if (this.row != 0) {
                        impl = this;
                        continue label85;
                     }

                     if (this.column != 0) {
                        break label90;
                     }
                  }
               } while (1L >= longValue);

               return this;
            }

            impl = this;
         }
      }

      // $VF: Irreducible bytecode was duplicated to produce valid code
      @Override
      public SlotIterator next() {
         long longValue = 31262812372841L;
         long longValue2 = longValue ^ 123703351150707L;
         PacketWrapper[] packetWrappers = IterationType.getPacketWrappers();
         if (this.ended()) {
            this.started = true;
            return this;
         }

         label65:
         while (true) {
            if (!this.started) {
               this.started = true;
               Object[] objects = packetWrappers;
               if (longValue >= 1L) {
                  Object[] objects2 = new Object[1];
                  objects = objects2;
                  objects2[0] = longValue2;
               }

               if (this.fn(objects)) {
                  break;
               }

               if (!this.ended()) {
                  continue;
               }

               if (1L <= longValue) {
                  break;
               }
            }

            do {
               SlotIterator.Impl impl = switch (this.iterationType) {
                  case HORIZONTAL -> {
                     this.column = ++this.column % this.smartInventory.getColumns();
                     if (this.column == 0) {
                        this.row++;
                     }

                     yield this;
                  }
                  case VERTICAL -> {
                     this.row = ++this.row % this.smartInventory.getRows();
                     if (this.row == 0) {
                        this.column++;
                     }
                     yield this;
                  }
                  default -> this;
               };
               Object[] objects3 = packetWrappers;
               if (longValue >= 1L) {
                  Object[] objects4 = new Object[1];
                  objects3 = objects4;
                  objects4[0] = longValue2;
               }

               if (impl.fn(objects3)) {
                  return this;
               }

               if (!this.ended()) {
                  continue label65;
               }
            } while (1L > longValue);

            return this;
         }

         return this;
      }

      @Override
      public SlotIterator blacklist(int value, int value2) {
         this.blacklisted.add(SlotPos.of(value, value2));
         return this;
      }

      @Override
      public SlotIterator blacklist(SlotPos slotPos) {
         return this.blacklist(slotPos.getRow(), slotPos.getColumn());
      }

      @Override
      public int row() {
         return this.row;
      }

      @Override
      public SlotIterator row(int value) {
         this.row = value;
         return this;
      }

      @Override
      public int column() {
         return this.column;
      }

      @Override
      public SlotIterator column(int value) {
         this.column = value;
         return this;
      }

      @Override
      public boolean started() {
         return this.started;
      }

      @Override
      public boolean ended() {
         long longValue = 1809869833049L;
         int value = this.row;
         int value2 = this.smartInventory.getRows();
         if (longValue > 0L) {
            value2 -= 1;
         }

         return value == value2 && this.column == this.smartInventory.getColumns() - 1;
      }

      @Override
      public boolean doesAllowOverride() {
         return this.allowOverride;
      }

      @Override
      public SlotIterator allowOverride(boolean flag) {
         this.allowOverride = flag;
         return this;
      }

      private boolean fn(Object[] objects) {
         long longValue = (Long)objects[0];
         longValue = a ^ longValue;
         long longValue2 = longValue ^ 94707633183513L;
         return !this.blacklisted.contains(SlotPos.of(this.row, this.column)) && (this.allowOverride || !this.get(longValue2).isPresent());
      }

   }
}
