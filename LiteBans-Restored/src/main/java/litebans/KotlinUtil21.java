package litebans;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class KotlinUtil21 {
   public static final int computeIntValue(@NotNull int[] ints, @NotNull KotlinRandom kotlinRandom) {
      return ints.length <= 1 ? ArraysKt.first(ints) : ints[kotlinRandom.nextInt(ints.length)];
   }

   public static final byte computeByteValue(@NotNull byte[] bytes, @NotNull KotlinRandom kotlinRandom) {
      return bytes.length <= 1 ? ArraysKt.first(bytes) : bytes[kotlinRandom.nextIntLenient(bytes.length)];
   }

   public static final byte computeByteValue2(@NotNull byte[] bytes, @NotNull KotlinRandom kotlinRandom) {
      return bytes[kotlinRandom.nextIntLenient(bytes.length)];
   }

   public static final byte computeByteValue3(@NotNull byte[] bytes, @NotNull KotlinRandom kotlinRandom) {
      return KotlinCollectionImpl.access$length(bytes) <= 1 ? UByte.constructorImpl(ArraysKt.first(bytes)) : KotlinCollectionImpl.computeByteValue(bytes, kotlinRandom.nextInt(KotlinCollectionImpl.access$length(bytes)));
   }

   public static final Object computeObj(@NotNull Object[] objs, @NotNull KotlinRandom kotlinRandom) {
      return objs.length <= 1 ? ArraysKt.first(objs) : objs[kotlinRandom.nextInt(objs.length)];
   }

   public static final byte[] computeBytes(@NotNull byte[] bytes, @NotNull KotlinRandom kotlinRandom) {
      byte[] bytes2 = bytes;

      for (int i = ArraysKt.getLastIndex(bytes2); 0 < i; i--) {
         int value = kotlinRandom.nextIntLenient(i + 1);
         byte b = bytes2[i];
         bytes2[i] = bytes2[value];
         bytes2[value] = b;
      }

      return bytes2;
   }

   public static final byte[] computeBytes(@NotNull byte[] bytes, @NotNull KotlinRandom kotlinRandom, int value) {
      byte[] bytes2 = bytes;

      for (int i = 0; i < value; i++) {
         int value2 = kotlinRandom.nextIntLenient(bytes2.length);
         int value3 = kotlinRandom.nextIntLenient(bytes2.length - 1);
         byte b = bytes2[value3];
         bytes2[value2] = b;
         bytes2[value3 + 1] = b;
      }

      return bytes2;
   }

   public static final List computeList(@NotNull List list, @NotNull KotlinRandom kotlinRandom) {
      List list2 = list;

      for (int i = CollectionsKt.getLastIndex(list2); 0 < i; i--) {
         int value = kotlinRandom.nextIntLenient(i + 1);
         list2.set(value, list2.set(i, list2.get(value)));
      }

      return list2;
   }

   public static final Object access$size(@NotNull List list, @NotNull KotlinRandom kotlinRandom) {
      return list.size() <= 1 ? CollectionsKt.first(list) : list.get(kotlinRandom.nextIntLenient(list.size()));
   }

   public static final Object access$size2(@NotNull Collection collection, @NotNull KotlinRandom kotlinRandom) {
      return collection.size() <= 1 ? CollectionsKt.first(collection) : CollectionsKt.elementAt(collection, kotlinRandom.nextIntLenient(collection.size()));
   }

   public static final Object access$isEmpty(@NotNull Collection collection, @NotNull KotlinRandom kotlinRandom) {
      return collection.isEmpty() ? null : access$size2(collection, kotlinRandom);
   }

   public static final void handleBytes(@NotNull byte[] bytes, @NotNull KotlinRandom kotlinRandom, @NotNull Number number) {
      bytes[kotlinRandom.nextIntLenient(bytes.length)] = number.byteValue();
   }

   public static final List computeList2(@NotNull List list, @NotNull KotlinRandom kotlinRandom) {
      List list2 = list;
      list2.remove(kotlinRandom.nextIntLenient(list2.size()));
      return list2;
   }
}
