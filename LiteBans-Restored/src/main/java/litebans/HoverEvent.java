package litebans;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HoverEvent {
   private final LiteShowTextEnum liteShowTextEnum;
   private final List list3;
   private boolean flag2 = false;

   @Deprecated
   public HoverEvent(LiteShowTextEnum liteShowTextEnum2, ChatBaseComponent[] chatBaseComponents) {
      this.liteShowTextEnum = liteShowTextEnum2;
      this.list3 = new ArrayList<>(Collections.singletonList(new ChatText(chatBaseComponents)));
      this.flag2 = true;
   }

   public static Class computeClass(LiteShowTextEnum liteShowTextEnum2, boolean flag) {
      Preconditions.checkArgument(liteShowTextEnum2 != null, "action");
      switch (liteShowTextEnum2) {
         case SHOW_TEXT:
            return flag ? ChatText[].class : ChatText.class;
         default:
            throw new UnsupportedOperationException("Action '" + liteShowTextEnum2.name() + " not supported");
      }
   }

   public LiteShowTextEnum getLiteShowTextEnum() {
      return this.liteShowTextEnum;
   }

   public List getList3() {
      return this.list3;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   @Override
   public String toString() {
      return "HoverEvent(action=" + this.getLiteShowTextEnum() + ", contents=" + this.getList3() + ", legacy=" + this.isFlag2() + ")";
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof HoverEvent)) {
         return false;
      } else {
         HoverEvent hoverEvent = (HoverEvent)obj;
         if (this.isFlag2() != hoverEvent.isFlag2()) {
            return false;
         } else {
            LiteShowTextEnum liteShowTextEnum2 = this.getLiteShowTextEnum();
            LiteShowTextEnum liteShowTextEnum3 = hoverEvent.getLiteShowTextEnum();
            if (liteShowTextEnum2 == null ? liteShowTextEnum3 == null : liteShowTextEnum2.equals(liteShowTextEnum3)) {
               List list = this.getList3();
               List list2 = hoverEvent.getList3();
               return list == null ? list2 == null : list.equals(list2);
            } else {
               return false;
            }
         }
      }
   }

   @Override
   public int hashCode() {
      byte b = 1;
      int value = b * 59 + (this.isFlag2() ? 79 : 97);
      LiteShowTextEnum liteShowTextEnum2 = this.getLiteShowTextEnum();
      int value2 = value * 59 + (liteShowTextEnum2 == null ? 43 : liteShowTextEnum2.hashCode());
      List list = this.getList3();
      return value2 * 59 + (list == null ? 43 : list.hashCode());
   }

   public HoverEvent(LiteShowTextEnum liteShowTextEnum2, List list) {
      this.liteShowTextEnum = liteShowTextEnum2;
      this.list3 = list;
   }
}
