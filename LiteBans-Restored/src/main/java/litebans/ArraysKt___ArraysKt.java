package litebans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ArraysKt___ArraysKt extends KotlinUtil27 {
   public static final boolean contains(@NotNull Object[] objs, Object obj) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      return ArraysKt.indexOf(objs, (Object)obj) >= 0;
   }

   public static final boolean contains(@NotNull char[] chars, char c) {
      Intrinsics.checkParameterIsNotNull(chars, "<this>");
      return ArraysKt.indexOf(chars, c) >= 0;
   }

   public static final Object first(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      if (objs.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return objs[0];
      }
   }

   public static final byte first(@NotNull byte[] bytes) {
      Intrinsics.checkParameterIsNotNull(bytes, "<this>");
      if (bytes.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return bytes[0];
      }
   }

   public static final int first(@NotNull int[] ints) {
      Intrinsics.checkParameterIsNotNull(ints, "<this>");
      if (ints.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return ints[0];
      }
   }

   public static final Object firstOrNull(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      return objs.length == 0 ? null : objs[0];
   }

   public static final Object getOrNull(@NotNull Object[] objs, int value) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      return (0 <= value ? value < objs.length : false) ? objs[value] : null;
   }

   public static final int indexOf(@NotNull Object[] objs, Object obj) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      if (obj == null) {
         int value = 0;

         for (int i = objs.length; value < i; value++) {
            if (objs[value] == null) {
               return value;
            }
         }
      } else {
         int value2 = 0;

         for (int i2 = objs.length; value2 < i2; value2++) {
            if (Intrinsics.areEqual(obj, objs[value2])) {
               return value2;
            }
         }
      }

      return -1;
   }

   public static final int indexOf(@NotNull char[] chars, char c) {
      Intrinsics.checkParameterIsNotNull(chars, "<this>");
      int value = 0;

      for (int i = chars.length; value < i; value++) {
         if (c == chars[value]) {
            return value;
         }
      }

      return -1;
   }

   public static final Object last(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      if (objs.length == 0) {
         throw new NoSuchElementException("Array is empty.");
      } else {
         return objs[ArraysKt.getLastIndex(objs)];
      }
   }

   public static final char single(@NotNull char[] chars) {
      Intrinsics.checkParameterIsNotNull(chars, "<this>");
      switch (chars.length) {
         case 0:
            throw new NoSuchElementException("Array is empty.");
         case 1:
            return chars[0];
         default:
            throw new IllegalArgumentException("Array has more than one element.");
      }
   }

   public static final int getLastIndex(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      return objs.length - 1;
   }

   public static final int getLastIndex(@NotNull byte[] bytes) {
      Intrinsics.checkParameterIsNotNull(bytes, "<this>");
      return bytes.length - 1;
   }

   public static final Collection toCollection(@NotNull Object[] objs, @NotNull Collection collection) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      Intrinsics.checkParameterIsNotNull(collection, "destination");
      int value = 0;

      for (int i = objs.length; value < i; value++) {
         Object obj = objs[value];
         collection.add(obj);
      }

      return collection;
   }

   public static final Collection toCollection(@NotNull char[] chars, @NotNull Collection collection) {
      Intrinsics.checkParameterIsNotNull(chars, "<this>");
      Intrinsics.checkParameterIsNotNull(collection, "destination");
      int value = 0;

      for (int i = chars.length; value < i; value++) {
         char c = chars[value];
         collection.add(c);
      }

      return collection;
   }

   public static final HashSet toHashSet(@NotNull char[] chars) {
      Intrinsics.checkParameterIsNotNull(chars, "<this>");
      return (HashSet)ArraysKt.toCollection(chars, new HashSet(KotlinStub15.computeIntValue(KotlinRangesKtSub.coerceAtMost(chars.length, 128))));
   }

   public static final List toMutableList(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      return new ArrayList(CollectionsKt.asCollection(objs));
   }

   public static final Set toSet(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      Set set;
      switch (objs.length) {
         case 0:
            set = KotlinStub.obtainSet();
            break;
         case 1:
            set = KotlinStub.computeSet(objs[0]);
            break;
         default:
            set = (Set)ArraysKt.toCollection((Object[])objs, new LinkedHashSet(KotlinStub15.computeIntValue(objs.length)));
      }

      return set;
   }

   public static final Appendable joinTo(
      @NotNull Object[] objs,
   @NotNull Appendable appendable,
   @NotNull CharSequence message,
   @NotNull CharSequence message2,
   @NotNull CharSequence message3,
   int value,
   @NotNull CharSequence message4,
   @Nullable Function1 callback
   ) {
      try {
         Intrinsics.checkParameterIsNotNull(objs, "<this>");
         Intrinsics.checkParameterIsNotNull(appendable, "buffer");
         Intrinsics.checkParameterIsNotNull(message, "separator");
         Intrinsics.checkParameterIsNotNull(message2, "prefix");
         Intrinsics.checkParameterIsNotNull(message3, "postfix");
         Intrinsics.checkParameterIsNotNull(message4, "truncated");
         appendable.append(message2);
         int value2 = 0;
         int value3 = 0;

         for (int i = objs.length; value3 < i; value3++) {
            Object obj = objs[value3];
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

   public static final Appendable joinTo(
      @NotNull byte[] bytes,
   @NotNull Appendable appendable,
   @NotNull CharSequence message,
   @NotNull CharSequence message2,
   @NotNull CharSequence message3,
   int value,
   @NotNull CharSequence message4,
   @Nullable Function1 callback
   ) {
      try {
         Intrinsics.checkParameterIsNotNull(bytes, "<this>");
         Intrinsics.checkParameterIsNotNull(appendable, "buffer");
         Intrinsics.checkParameterIsNotNull(message, "separator");
         Intrinsics.checkParameterIsNotNull(message2, "prefix");
         Intrinsics.checkParameterIsNotNull(message3, "postfix");
         Intrinsics.checkParameterIsNotNull(message4, "truncated");
         appendable.append(message2);
         int value2 = 0;
         int value3 = 0;

         for (int i = bytes.length; value3 < i; value3++) {
            byte b = bytes[value3];
            if (++value2 > 1) {
               appendable.append(message);
            }

            if (value >= 0 && value2 > value) {
               break;
            }

            if (callback != null) {
               appendable.append((CharSequence)callback.computeObj(b));
            } else {
               appendable.append(String.valueOf(b));
            }
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
      @NotNull Object[] objs,
   @NotNull CharSequence message,
   @NotNull CharSequence message2,
   @NotNull CharSequence message3,
   int value,
   @NotNull CharSequence message4,
   @Nullable Function1 callback
   ) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      Intrinsics.checkParameterIsNotNull(message, "separator");
      Intrinsics.checkParameterIsNotNull(message2, "prefix");
      Intrinsics.checkParameterIsNotNull(message3, "postfix");
      Intrinsics.checkParameterIsNotNull(message4, "truncated");
      return ((StringBuilder)ArraysKt.joinTo((Object[])objs, new StringBuilder(), message, message2, message3, value, message4, callback)).toString();
   }

   public static String joinToStringWithDefaults(Object[] objs, CharSequence message, CharSequence message2, CharSequence message3, int value, CharSequence message4, Function1 callback, int value2, Object obj) {
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

      return ArraysKt.joinToString((Object[])objs, message, message2, message3, value, message4, callback);
   }

   public static final String joinToString(
      @NotNull byte[] bytes,
   @NotNull CharSequence message,
   @NotNull CharSequence message2,
   @NotNull CharSequence message3,
   int value,
   @NotNull CharSequence message4,
   @Nullable Function1 callback
   ) {
      Intrinsics.checkParameterIsNotNull(bytes, "<this>");
      Intrinsics.checkParameterIsNotNull(message, "separator");
      Intrinsics.checkParameterIsNotNull(message2, "prefix");
      Intrinsics.checkParameterIsNotNull(message3, "postfix");
      Intrinsics.checkParameterIsNotNull(message4, "truncated");
      return ((StringBuilder)ArraysKt.joinTo(bytes, new StringBuilder(), message, message2, message3, value, message4, callback)).toString();
   }

   public static String joinToStringWithDefaults(byte[] bytes, CharSequence message, CharSequence message2, CharSequence message3, int value, CharSequence message4, Function1 callback, int value2, Object obj) {
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

      return ArraysKt.joinToString(bytes, message, message2, message3, value, message4, callback);
   }

   public static final int sum(@NotNull int[] ints) {
      Intrinsics.checkParameterIsNotNull(ints, "<this>");
      int value = 0;
      int value2 = 0;

      for (int i = ints.length; value2 < i; value2++) {
         int value3 = ints[value2];
         value += value3;
      }

      return value;
   }
}
