package io.lumine.mythic.bukkit.utils.collections;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class Indexing {
   private Indexing() {
   }

   public static <I, R> Map<I, R> build(Iterable<? extends R> values, Function<? super R, ? extends I> indexFunction) {
      Objects.requireNonNull(indexFunction, "indexFunction");
      return buildMultiple(values, r -> Collections.singleton(indexFunction.apply(r)));
   }

   public static <I, R> Map<I, R> buildMultiple(Iterable<? extends R> values, Function<? super R, ? extends Iterable<? extends I>> indexFunction) {
      Objects.requireNonNull(values, "values");
      Objects.requireNonNull(indexFunction, "indexFunction");
      Map<I, R> map = new HashMap<>();

      for (R value : values) {
         for (I index : indexFunction.apply(value)) {
            R prev = map.put(index, value);
            if (prev != null) {
               throw new IllegalStateException("An index for " + value + " (" + index + ") was already associated with " + prev);
            }
         }
      }

      return ImmutableMap.copyOf(map);
   }

   public static <I, R> Map<I, R> build(R[] values, Function<? super R, ? extends I> indexFunction) {
      Objects.requireNonNull(values, "values");
      return build(Arrays.asList(values), indexFunction);
   }

   public static <I, R> Map<I, R> buildMultiple(R[] values, Function<? super R, ? extends Iterable<? extends I>> indexFunction) {
      Objects.requireNonNull(values, "values");
      return buildMultiple(Arrays.asList(values), indexFunction);
   }

   public static <R extends Enum<?>> Map<String, R> buildFromEnumName(Class<? extends R> enumClass) {
      Objects.requireNonNull(enumClass, "enumClass");
      R[] enumConstants = (R[])enumClass.getEnumConstants();
      if (enumConstants == null) {
         throw new IllegalArgumentException("Type is not an enum: " + enumClass);
      } else {
         return build(enumConstants, Enum::name);
      }
   }
}
