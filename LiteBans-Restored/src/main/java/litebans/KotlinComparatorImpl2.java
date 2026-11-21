package litebans;

import java.util.Comparator;

public final class KotlinComparatorImpl2 implements Comparator {
   final KotlinData5 kotlinData;

   public KotlinComparatorImpl2(KotlinData5 kotlinData) {
      this.kotlinData = kotlinData;
   }

   @Override
   public final int compare(Object obj, Object obj2) {
      String string = (String)obj2;
      CharSequence message = string;
      int value = 0;

      for (int i = 0; i < message.length(); i++) {
         char charAt2 = message.charAt(i);
         int value2 = value;
         int value3 = this.kotlinData.computeIntValue(charAt2);
         value = value2 + value3;
      }

      Comparable comparable = value;
      String string2 = (String)obj;
      CharSequence message2 = string2;
      int value4 = 0;

      for (int i2 = 0; i2 < message2.length(); i2++) {
         char charAt3 = message2.charAt(i2);
         int value5 = value4;
         int value6 = this.kotlinData.computeIntValue(charAt3);
         value4 = value5 + value6;
      }

      return KotlinCollection.computeIntValue(comparable, value4);
   }
}
