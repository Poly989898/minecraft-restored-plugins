package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.stat.data.type.StatData;

public class BooleanData implements StatData {
   private final boolean state;

   public BooleanData(boolean flag) {
      this.state = flag;
   }

   public boolean isEnabled() {
      return this.state;
   }

   @Override
   public boolean isEmpty() {
      return !this.state;
   }

   @Override
   public String toString() {
      return "" + this.state;
   }
}
