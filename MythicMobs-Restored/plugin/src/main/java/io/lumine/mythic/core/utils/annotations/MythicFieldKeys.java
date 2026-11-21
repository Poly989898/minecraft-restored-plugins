package io.lumine.mythic.core.utils.annotations;

import java.lang.reflect.Field;

public final class MythicFieldKeys {
   private MythicFieldKeys() {
   }

   public static String[] keys(Class<?> clazz, String fieldName) {
      for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
         try {
            Field field = current.getDeclaredField(fieldName);
            MythicField mf = field.getAnnotation(MythicField.class);
            if (mf == null) {
               return new String[]{fieldName};
            }

            String primary = mf.name().isEmpty() ? fieldName : mf.name();
            String[] aliases = mf.aliases();
            String[] result = new String[aliases.length + 1];
            result[0] = primary;
            System.arraycopy(aliases, 0, result, 1, aliases.length);
            return result;
         } catch (NoSuchFieldException e) {
         }
      }

      return new String[]{fieldName};
   }
}
