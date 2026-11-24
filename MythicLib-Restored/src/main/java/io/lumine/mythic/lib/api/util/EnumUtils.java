package io.lumine.mythic.lib.api.util;

import java.util.Optional;

public class EnumUtils {
   public static <T> Optional<T> getIfPresent(Class<T> enumClass, String key) {
      if (!enumClass.isEnum()) {
         return Optional.empty();
      }

      for (T constant : enumClass.getEnumConstants()) {
         if (((Enum)constant).name().equals(key)) {
            return Optional.of(constant);
         }
      }

      return Optional.empty();
   }
}
