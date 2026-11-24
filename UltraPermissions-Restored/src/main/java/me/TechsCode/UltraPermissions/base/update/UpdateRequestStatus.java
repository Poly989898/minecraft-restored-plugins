package me.TechsCode.UltraPermissions.base.update;

public enum UpdateRequestStatus {
   WAITING_AUTHENTICATION,
   AUTHORIZED,
   UNAUTHORIZED,
   UPDATED,
   UNKNOWN;

   UpdateRequestStatus() {
   }

   public static UpdateRequestStatus fromString(String text) {
      if (text.equalsIgnoreCase("waiting_authentication")) {
         return WAITING_AUTHENTICATION;
      } else if (text.equalsIgnoreCase("authorized")) {
         return AUTHORIZED;
      } else if (text.equalsIgnoreCase("unauthorized")) {
         return UNAUTHORIZED;
      } else {
         return text.equalsIgnoreCase("updated") ? UPDATED : UNKNOWN;
      }
   }
}
