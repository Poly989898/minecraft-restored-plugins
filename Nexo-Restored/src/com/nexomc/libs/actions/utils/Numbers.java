package com.nexomc.libs.actions.utils;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.jetbrains.annotations.Nullable;

public final class Numbers {
   private Numbers() {
      throw new IllegalArgumentException();
   }

   public static int tryParse(@Nullable String string, int def) {
      if (string == null) {
         return def;
      } else {
         Integer value = Ints.tryParse(string);
         return value == null ? def : value;
      }
   }

   public static double tryParse(@Nullable String string, double def) {
      Double result = tryParseDouble(string);
      return result == null ? def : result;
   }

   public static @Nullable Double tryParseDouble(@Nullable String string) {
      return string == null ? null : Doubles.tryParse(string);
   }

   public static long tryParse(@Nullable String string, long def) {
      Long result = tryParseLong(string);
      return result == null ? def : result;
   }

   public static @Nullable Long tryParseLong(@Nullable String string) {
      return string == null ? null : Longs.tryParse(string);
   }
}
