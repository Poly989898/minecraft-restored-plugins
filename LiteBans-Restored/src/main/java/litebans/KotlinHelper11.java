package litebans;

import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public final class KotlinHelper11 {
   private KotlinHelper11() {
   }

   public final Pair obtainPair() {
      return KotlinUtil26.obtainPair();
   }

   public final Pair computePair(@NotNull byte[] bytes, int value, int value2, @NotNull KotlinData5 kotlinData, boolean flag) {
      int value3 = bytes.length - value;
      int value4 = flag ? Math.min(1600, Math.min(bytes.length / 2, value3)) : value3;
      int value5 = 0;

      while (value5 < value4) {
         int value6 = value5++;
         var list = kotlinData.getList2();
         boolean flag2;
         if (list instanceof Collection && ((Collection)list).isEmpty()) {
            flag2 = false;
         } else {
            Iterator iterator2 = list.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag2 = false;
                  break;
               }

               Object obj = iterator2.next();
               KotlinUtil26 kotlinUtil26 = (KotlinUtil26)obj;
               if (kotlinUtil26.checkBytes(bytes, value6, value6 + value)) {
                  flag2 = true;
                  break;
               }
            }
         }

         if (!flag2) {
            short value7 = this.computeShortValue(bytes, value4, value6, value, value2, flag);
            if ((value7 & '\uffff') > value2) {
               return this.computePair(value, bytes, value6, value7);
            }
         }
      }

      return this.obtainPair();
   }

   private final Pair computePair(int value, byte[] bytes, int value2, short value3) {
      byte[] bytes2 = new byte[value];
      System.arraycopy(bytes, value2, bytes2, 0, value);
      return TuplesKt.computePair(ULong.box(value3), bytes2);
   }

   private final short computeShortValue(byte[] bytes, int value, int value2, int value3, int value4, boolean flag) {
      short value5 = 0;
      int value6 = Integer.MIN_VALUE;
      int value7 = flag ? 568 : Integer.MAX_VALUE;
      int value8 = 0;
      int value9 = 0;

      for (int i = bytes.length; value9 < i; value9++) {
         byte b = bytes[value9];
         int value10 = value8++;
         if (value10 > value7 && (value5 & '\uffff') < value4) {
            return value5;
         }

         if ((value6 <= value10 ? value10 < value : false) && StringMatching.regionEquals(bytes, bytes, value2, value10, value10 + value3)) {
            value6 = value10 + value3;
            value5 = ULong.constructorImpl((short)(value5 + 1));
         }
      }

      return value5;
   }

   public KotlinHelper11(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
