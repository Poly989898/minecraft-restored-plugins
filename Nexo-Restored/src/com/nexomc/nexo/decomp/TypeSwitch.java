package com.nexomc.nexo.decomp;

/**
 * Replacement for the invokedynamic typeSwitch bootstrap that the decompiler could not express in
 * source form. Mirrors its semantics: -1 for null, index of the first matching type, otherwise
 * types.length.
 */
public final class TypeSwitch {
   private TypeSwitch() {
   }

   public static int index(Object value, Class<?>... types) {
      if (value == null) {
         return -1;
      }
      for (int i = 0; i < types.length; i++) {
         if (types[i].isInstance(value)) {
            return i;
         }
      }
      return types.length;
   }
}
