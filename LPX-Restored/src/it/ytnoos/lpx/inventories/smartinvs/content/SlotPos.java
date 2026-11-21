package it.ytnoos.lpx.inventories.smartinvs.content;


public class SlotPos {
   private final int row;
   private final int column;

   public SlotPos(int value, int value2) {
      this.row = value;
      this.column = value2;
   }

   public static SlotPos of(int value, int value2) {
      return new SlotPos(value, value2);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         SlotPos slotPos = (SlotPos)object;
         return this.row == slotPos.row && this.column == slotPos.column;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int value = this.row;
      return 31 * value + this.column;
   }

   public int getRow() {
      return this.row;
   }

   public int getColumn() {
      return this.column;
   }

}
