package litebans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CollectionsKt___CollectionsKt extends KotlinUtil34 {
   public static final boolean contains(@NotNull Iterable iterable, Object obj) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      return iterable instanceof Collection ? ((Collection)iterable).contains(obj) : CollectionsKt.indexOf(iterable, obj) >= 0;
   }

   public static final Object elementAt(@NotNull Iterable iterable, int value) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      return iterable instanceof List ? ((List)iterable).get(value) : CollectionsKt.elementAtOrElse(iterable, value, obj -> {
         throw new IndexOutOfBoundsException("Collection doesn't contain element at index " + value + '.');
      });
   }

   public static final Object elementAtOrElse(@NotNull Iterable iterable, int value, @NotNull Function1 callback) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      Intrinsics.checkParameterIsNotNull(callback, "defaultValue");
      if (iterable instanceof List) {
         List list = (List)iterable;
         return (0 <= value ? value < list.size() : false) ? list.get(value) : callback.computeObj(value);
      }

      if (value < 0) {
         return callback.computeObj(value);
      }

      Iterator iterator2 = iterable.iterator();
      int value2 = 0;

      while (iterator2.hasNext()) {
         Object obj = iterator2.next();
         if (value == value2++) {
            return obj;
         }
      }

      return callback.computeObj(value);
   }

   public static final Object first(@NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      if (iterable instanceof List) {
         return CollectionsKt.first((List)iterable);
      } else {
         Iterator iterator2 = iterable.iterator();
         if (!iterator2.hasNext()) {
            throw new NoSuchElementException("Collection is empty.");
         } else {
            return iterator2.next();
         }
      }
   }

   public static final Object first(@NotNull List list) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      if (list.isEmpty()) {
         throw new NoSuchElementException("List is empty.");
      } else {
         return list.get(0);
      }
   }

   public static final Object firstOrNull(@NotNull List list) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      return list.isEmpty() ? null : list.get(0);
   }

   public static final Object getOrNull(@NotNull List list, int value) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      return (0 <= value ? value < list.size() : false) ? list.get(value) : null;
   }

   public static final int indexOf(@NotNull Iterable iterable, Object obj) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      if (iterable instanceof List) {
         return ((List)iterable).indexOf(obj);
      }

      int value = 0;

      for (Object obj2 : iterable) {
         if (value < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         if (Intrinsics.areEqual(obj, obj2)) {
            return value;
         }

         value++;
      }

      return -1;
   }

   public static final Object last(@NotNull List list) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      if (list.isEmpty()) {
         throw new NoSuchElementException("List is empty.");
      } else {
         return list.get(CollectionsKt.getLastIndex(list));
      }
   }

   public static final Object single(@NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      if (iterable instanceof List) {
         return CollectionsKt.single((List)iterable);
      } else {
         Iterator iterator2 = iterable.iterator();
         if (!iterator2.hasNext()) {
            throw new NoSuchElementException("Collection is empty.");
         } else {
            Object obj = iterator2.next();
            if (iterator2.hasNext()) {
               throw new IllegalArgumentException("Collection has more than one element.");
            } else {
               return obj;
            }
         }
      }
   }

   public static final Object single(@NotNull List list) {
      Intrinsics.checkParameterIsNotNull(list, "<this>");
      switch (list.size()) {
         case 0:
            throw new NoSuchElementException("List is empty.");
         case 1:
            return list.get(0);
         default:
            throw new IllegalArgumentException("List has more than one element.");
      }
   }

   public static final List take(@NotNull Iterable iterable, int value) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      if (value < 0) {
         String text = "Requested element count " + value + " is less than zero.";
         throw new IllegalArgumentException(text.toString());
      }

      if (value == 0) {
         return CollectionsKt.emptyList();
      }

      if (iterable instanceof Collection) {
         if (value >= ((Collection)iterable).size()) {
            return CollectionsKt.toList(iterable);
         }

         if (value == 1) {
            return CollectionsKt.computeList(CollectionsKt.first(iterable));
         }
      }

      int value2 = 0;
      ArrayList arrayList = new ArrayList(value);

      for (Object obj : iterable) {
         arrayList.add(obj);
         if (++value2 == value) {
            break;
         }
      }

      return CollectionsKt.optimizeReadOnlyList(arrayList);
   }

   public static final List reversed(@NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      if (iterable instanceof Collection && ((Collection)iterable).size() <= 1) {
         return CollectionsKt.toList(iterable);
      }

      List mutableList = CollectionsKt.toMutableList(iterable);
      CollectionsKt.handleList2(mutableList);
      return mutableList;
   }

   public static final List sorted(@NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      if (iterable instanceof Collection) {
         if (((Collection)iterable).size() <= 1) {
            return CollectionsKt.toList(iterable);
         }

         Collection collection = (Collection)iterable;
         Object[] array = collection.toArray(new Comparable[0]);
         Comparable[] comparables = (Comparable[])array;
         ArraysKt.handleObjArray(comparables);
         return ArraysKt.computeList(array);
      } else {
         List mutableList = CollectionsKt.toMutableList(iterable);
         CollectionsKt.handleList(mutableList);
         return mutableList;
      }
   }

   public static final List sortedWith(@NotNull Iterable iterable, @NotNull Comparator comparator) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      Intrinsics.checkParameterIsNotNull(comparator, "comparator");
      if (iterable instanceof Collection) {
         if (((Collection)iterable).size() <= 1) {
            return CollectionsKt.toList(iterable);
         }

         Collection collection = (Collection)iterable;
         Object[] array = (Object[])collection.toArray(new Object[0]);
         ArraysKt.handleObjArray(array, comparator);
         return ArraysKt.computeList(array);
      } else {
         List mutableList = CollectionsKt.toMutableList(iterable);
         CollectionsKt.handleList(mutableList, comparator);
         return mutableList;
      }
   }

   public static final Collection toCollection(@NotNull Iterable iterable, @NotNull Collection collection) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      Intrinsics.checkParameterIsNotNull(collection, "destination");

      for (Object obj : iterable) {
         collection.add(obj);
      }

      return collection;
   }

   public static final HashSet toHashSet(@NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      return (HashSet)CollectionsKt.toCollection((Iterable)iterable, new HashSet(KotlinStub15.computeIntValue(CollectionsKt.computeIntValue((Iterable)iterable, 12))));
   }

   public static final List toList(@NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      if (iterable instanceof Collection) {
         List list;
         switch (((Collection)iterable).size()) {
            case 0:
               list = CollectionsKt.emptyList();
               break;
            case 1:
               list = CollectionsKt.computeList(iterable instanceof List ? ((List)iterable).get(0) : ((Collection)iterable).iterator().next());
               break;
            default:
               list = CollectionsKt.toMutableList((Collection)iterable);
         }

         return list;
      } else {
         return CollectionsKt.optimizeReadOnlyList(CollectionsKt.toMutableList(iterable));
      }
   }

   public static final List toMutableList(@NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      return iterable instanceof Collection ? CollectionsKt.toMutableList((Collection)iterable) : (List)CollectionsKt.toCollection((Iterable)iterable, new ArrayList());
   }

   public static final List toMutableList(@NotNull Collection collection) {
      Intrinsics.checkParameterIsNotNull(collection, "<this>");
      return new ArrayList(collection);
   }

   public static final Set toSet(@NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      if (iterable instanceof Collection) {
         Set set;
         switch (((Collection)iterable).size()) {
            case 0:
               set = KotlinStub.obtainSet();
               break;
            case 1:
               set = KotlinStub.computeSet(iterable instanceof List ? ((List)iterable).get(0) : ((Collection)iterable).iterator().next());
               break;
            default:
               set = (Set)CollectionsKt.toCollection((Iterable)iterable, new LinkedHashSet(KotlinStub15.computeIntValue(((Collection)iterable).size())));
         }

         return set;
      } else {
         return KotlinStub.computeSet((Set)CollectionsKt.toCollection((Iterable)iterable, new LinkedHashSet()));
      }
   }

   public static final List plus(@NotNull Collection collection, Object obj) {
      Intrinsics.checkParameterIsNotNull(collection, "<this>");
      ArrayList arrayList = new ArrayList(collection.size() + 1);
      arrayList.addAll(collection);
      arrayList.add(obj);
      return arrayList;
   }

   public static final List plus(@NotNull Collection collection, @NotNull Iterable iterable) {
      Intrinsics.checkParameterIsNotNull(collection, "<this>");
      Intrinsics.checkParameterIsNotNull(iterable, "elements");
      if (iterable instanceof Collection) {
         ArrayList arrayList = new ArrayList(collection.size() + ((Collection)iterable).size());
         arrayList.addAll(collection);
         arrayList.addAll((Collection)iterable);
         return arrayList;
      } else {
         ArrayList arrayList2 = new ArrayList(collection);
         CollectionsKt.checkCollection(arrayList2, (Iterable)iterable);
         return arrayList2;
      }
   }

   public static final Appendable joinTo(
      @NotNull Iterable iterable,
   @NotNull Appendable appendable,
   @NotNull CharSequence message,
   @NotNull CharSequence message2,
   @NotNull CharSequence message3,
   int value,
   @NotNull CharSequence message4,
   @Nullable Function1 callback
   ) {
      try {
         Intrinsics.checkParameterIsNotNull(iterable, "<this>");
         Intrinsics.checkParameterIsNotNull(appendable, "buffer");
         Intrinsics.checkParameterIsNotNull(message, "separator");
         Intrinsics.checkParameterIsNotNull(message2, "prefix");
         Intrinsics.checkParameterIsNotNull(message3, "postfix");
         Intrinsics.checkParameterIsNotNull(message4, "truncated");
         appendable.append(message2);
         int value2 = 0;

         for (Object obj : iterable) {
            if (++value2 > 1) {
               appendable.append(message);
            }

            if (value >= 0 && value2 > value) {
               break;
            }

            StringsKt.handleAppendable(appendable, obj, callback);
         }

         if (value >= 0 && value2 > value) {
            appendable.append(message4);
         }

         appendable.append(message3);
         return appendable;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static final String joinToString(
      @NotNull Iterable iterable,
   @NotNull CharSequence message,
   @NotNull CharSequence message2,
   @NotNull CharSequence message3,
   int value,
   @NotNull CharSequence message4,
   @Nullable Function1 callback
   ) {
      Intrinsics.checkParameterIsNotNull(iterable, "<this>");
      Intrinsics.checkParameterIsNotNull(message, "separator");
      Intrinsics.checkParameterIsNotNull(message2, "prefix");
      Intrinsics.checkParameterIsNotNull(message3, "postfix");
      Intrinsics.checkParameterIsNotNull(message4, "truncated");
      return ((StringBuilder)CollectionsKt.joinTo(iterable, new StringBuilder(), message, message2, message3, value, message4, callback)).toString();
   }

   public static String joinToStringWithDefaults(Iterable iterable, CharSequence message, CharSequence message2, CharSequence message3, int value, CharSequence message4, Function1 callback, int value2, Object obj) {
      if ((value2 & 1) != 0) {
         message = ", ";
      }

      if ((value2 & 2) != 0) {
         message2 = "";
      }

      if ((value2 & 4) != 0) {
         message3 = "";
      }

      if ((value2 & 8) != 0) {
         value = -1;
      }

      if ((value2 & 16) != 0) {
         message4 = "...";
      }

      if ((value2 & 32) != 0) {
         callback = null;
      }

      return CollectionsKt.joinToString(iterable, message, message2, message3, value, message4, callback);
   }

}
