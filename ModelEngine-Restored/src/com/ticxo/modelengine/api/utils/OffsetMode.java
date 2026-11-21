package com.ticxo.modelengine.api.utils;

public enum OffsetMode {
   LOCAL,
   MODEL,
   GLOBAL;

   public static OffsetMode get(String name) {
      try {
         return valueOf(name);
      } catch (IllegalArgumentException ignored) {
         return LOCAL;
      }
   }
}
