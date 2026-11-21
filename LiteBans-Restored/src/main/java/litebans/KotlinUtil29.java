package litebans;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class KotlinUtil29 extends KotlinUtil28 {
   public static final List computeList(@NotNull Object[][] objsArray) {
      Intrinsics.checkParameterIsNotNull(objsArray, "<this>");
      Object[] objs = (Object[])objsArray;
      int value = 0;
      int value2 = 0;

      for (int i = objs.length; value2 < i; value2++) {
         Object obj = objs[value2];
         Object[] objs2 = (Object[])obj;
         int value3 = value;
         int length2 = objs2.length;
         value = value3 + length2;
      }

      int value4 = value;
      ArrayList arrayList = new ArrayList(value4);
      int value5 = 0;

      for (int i2 = ((Object[])objsArray).length; value5 < i2; value5++) {
         Object[] objs3 = objsArray[value5];
         CollectionsKt.checkCollection(arrayList, objs3);
      }

      return arrayList;
   }
}
