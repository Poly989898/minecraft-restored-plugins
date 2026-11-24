package me.TechsCode.UltraPermissions.base.update.responses;

public class IsAvailableResponse {
   private int statusCode;
   private boolean isAvailable;

   public IsAvailableResponse(int count) {
      this.statusCode = count;
      this.isAvailable = count == 200;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public boolean isAvailable() {
      return this.isAvailable;
   }
}
