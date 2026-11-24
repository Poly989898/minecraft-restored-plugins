package me.TechsCode.UltraPermissions.base.mysql;

public class ConnectionTestResult {
   private final boolean valid;
   private final String error;

   public ConnectionTestResult(boolean valid, String error) {
      this.valid = valid;
      this.error = error;
   }

   public boolean isValid() {
      return this.valid;
   }

   public String getError() {
      return this.error;
   }
}
