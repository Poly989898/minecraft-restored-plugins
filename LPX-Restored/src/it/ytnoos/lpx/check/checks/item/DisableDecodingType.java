package it.ytnoos.lpx.check.checks.item;

public enum DisableDecodingType {
   CREATIVE,
   ALL,
   SMART,
   NONE;

   public static final DisableDecodingType DEFAULT = SMART;

   DisableDecodingType() {
   }

   public static DisableDecodingType CD(String string) {
      try {
         return valueOf(string.toUpperCase());
      } catch (IllegalArgumentException illegalArgumentException) {
         return DEFAULT;
      }
   }
}
