package com.google.gson.internal;

public final class GsonPreconditions {
   private GsonPreconditions() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static <T> T checkNotNull(T obj) {
      if (obj == null) {
         throw new NullPointerException();
      } else {
         return obj;
      }
   }

   public static void checkArgument(boolean condition) {
      if (!condition) {
         throw new IllegalArgumentException();
      }
   }
}
