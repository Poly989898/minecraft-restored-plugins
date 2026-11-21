package litebans;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import org.jetbrains.annotations.NotNull;

class KotlinUtil37 extends KotlinUtil39 {
   public static final boolean checkCollection(@NotNull Collection collection, @NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(collection, "<this>");
      Intrinsics.checkParameterIsNotNull(iterable, "elements");
      if (iterable instanceof Collection) {
         return collection.addAll((Collection)iterable);
      }

      boolean flag = false;

      for (Object obj : iterable) {
         if (collection.add(obj)) {
            flag = true;
         }
      }

      return flag;
   }

   public static final boolean checkCollection(@NotNull Collection collection, @NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(collection, "<this>");
      Intrinsics.checkParameterIsNotNull(objs, "elements");
      return collection.addAll(ArraysKt.computeList(objs));
   }

   public static final boolean checkIterable(@NotNull Iterable iterable, @NotNull Function1 callback) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      Intrinsics.checkParameterIsNotNull(callback, "predicate");
      return checkIterable(iterable, callback, true);
   }

   private static final boolean checkIterable(Iterable iterable, Function1 callback, boolean flag) {
      boolean flag2 = false;
      Iterator iterator2 = iterable.iterator();

      while (iterator2.hasNext()) {
         if ((Boolean)callback.computeObj(iterator2.next()) == flag) {
            iterator2.remove();
            flag2 = true;
         }
      }

      return flag2;
   }

   public static final boolean checkList(@NotNull List list, @NotNull Function1 callback) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      Intrinsics.checkParameterIsNotNull(callback, "predicate");
      return checkList(list, callback, false);
   }

   private static final boolean checkList(List list, Function1 callback, boolean flag) {
      if (!(list instanceof RandomAccess)) {
         Intrinsics.checkNotNullExpressionValue(list, "");
         return checkIterable(KotlinUtil30.asMutableIterable(list), callback, flag);
      }

      int value = 0;
      int value2 = 0;
      int value3 = CollectionsKt.getLastIndex(list);
      if (value2 <= value3) {
         while (true) {
            Object obj = list.get(value2);
            if ((Boolean)callback.computeObj(obj) != flag) {
               if (value != value2) {
                  list.set(value, obj);
               }

               value++;
            }

            if (value2 == value3) {
               break;
            }

            value2++;
         }
      }

      if (value >= list.size()) {
         return false;
      }

      int value4 = CollectionsKt.getLastIndex(list);
      int value5 = value;
      if (value5 <= value4) {
         while (true) {
            list.remove(value4);
            if (value4 == value5) {
               break;
            }

            value4--;
         }
      }

      return true;
   }
}
