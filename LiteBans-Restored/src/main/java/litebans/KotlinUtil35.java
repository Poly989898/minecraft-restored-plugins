package litebans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class KotlinUtil35 extends KotlinUtil40 {
   public static final Collection asCollection(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      return new KotlinCollectionImpl3(objs, false);
   }

   public static final List emptyList() {
      return KotlinListImpl.return2;
   }

   public static final List listOf(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "elements");
      return objs.length > 0 ? ArraysKt.computeList(objs) : CollectionsKt.emptyList();
   }

   public static final List mutableListOf(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "elements");
      return objs.length == 0 ? new ArrayList() : new ArrayList(new KotlinCollectionImpl3(objs, true));
   }

   public static final ArrayList arrayListOf(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "elements");
      return objs.length == 0 ? new ArrayList() : new ArrayList(new KotlinCollectionImpl3(objs, true));
   }

   public static final int getLastIndex(@NotNull List list) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      return list.size() - 1;
   }

   public static final List optimizeReadOnlyList(@NotNull List list) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      List list2;
      switch (list.size()) {
         case 0:
            list2 = CollectionsKt.emptyList();
            break;
         case 1:
            list2 = CollectionsKt.computeList(list.get(0));
            break;
         default:
            list2 = list;
      }

      return list2;
   }

   public static final void throwIndexOverflow() {
      throw new ArithmeticException("Index overflow has happened.");
   }

   public static final void throwCountOverflow() {
      throw new ArithmeticException("Count overflow has happened.");
   }
}
