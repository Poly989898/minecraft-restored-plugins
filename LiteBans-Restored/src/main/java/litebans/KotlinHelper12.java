package litebans;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinHelper12 {
   private static final Object[] OBJ_ARRAY = new Object[0];
   private static final int VALUE_2147483645 = 2147483645;

   public static final Object[] computeObjArray(@NotNull Collection collection) {
      Intrinsics.checkParameterIsNotNull(collection, "collection");
      int value = collection.size();
      Object[] objs;
      if (value == 0) {
         objs = OBJ_ARRAY;
      } else {
         Iterator iterator2 = collection.iterator();
         if (!iterator2.hasNext()) {
            objs = OBJ_ARRAY;
         } else {
            Object[] objs2 = new Object[value];
            int value2 = 0;

            while (true) {
               objs2[value2++] = iterator2.next();
               if (value2 >= objs2.length) {
                  if (!iterator2.hasNext()) {
                     objs = objs2;
                     break;
                  }

                  int value3 = value2 * 3 + 1 >>> 1;
                  if (value3 <= value2) {
                     if (value2 >= 2147483645) {
                        throw new OutOfMemoryError();
                     }

                     value3 = 2147483645;
                  }

                  objs2 = Arrays.copyOf(objs2, value3);
               } else if (!iterator2.hasNext()) {
                  int value4 = value2;
                  Object[] objs3 = objs2;
                  objs = Arrays.copyOf(objs3, value4);
                  break;
               }
            }
         }
      }

      return objs;
   }

   public static final Object[] computeObjArray(@NotNull Collection collection, @Nullable Object[] objs) {
      Intrinsics.checkParameterIsNotNull(collection, "collection");
      if (objs == null) {
         throw new NullPointerException();
      }

      int value = collection.size();
      Object[] objs2;
      if (value == 0) {
         if (objs.length > 0) {
            objs[0] = null;
         }

         objs2 = objs;
      } else {
         Iterator iterator2 = collection.iterator();
         if (!iterator2.hasNext()) {
            if (objs.length > 0) {
               objs[0] = null;
            }

            objs2 = objs;
         } else {
            if (value <= objs.length) {
               objs2 = objs;
            } else {
               objs2 = (Object[])Array.newInstance(objs.getClass().getComponentType(), value);
               Intrinsics.checkNotNullExpressionValue(objs2, "");
               objs2 = objs2;
            }

            Object[] objs3 = objs2;
            int value2 = 0;

            while (true) {
               objs3[value2++] = iterator2.next();
               if (value2 >= objs3.length) {
                  if (!iterator2.hasNext()) {
                     objs2 = objs3;
                     break;
                  }

                  int value3 = value2 * 3 + 1 >>> 1;
                  if (value3 <= value2) {
                     if (value2 >= 2147483645) {
                        throw new OutOfMemoryError();
                     }

                     value3 = 2147483645;
                  }

                  objs3 = Arrays.copyOf(objs3, value3);
               } else if (!iterator2.hasNext()) {
                  int value4 = value2;
                  Object[] objs4 = objs3;
                  if (objs4 == objs) {
                     objs[value4] = null;
                     objs2 = objs;
                  } else {
                     objs2 = Arrays.copyOf(objs4, value4);
                  }
                  break;
               }
            }
         }
      }

      return objs2;
   }
}
