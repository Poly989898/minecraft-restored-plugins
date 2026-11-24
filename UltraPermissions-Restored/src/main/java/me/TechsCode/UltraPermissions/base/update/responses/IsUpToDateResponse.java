package me.TechsCode.UltraPermissions.base.update.responses;

public class IsUpToDateResponse {
   private int statusCode;
   private boolean isUpToDate;
   private String latestVersion;
   private String message;

   public IsUpToDateResponse(int code, boolean upToDate, String latestVersion, String message) {
      this.statusCode = code;
      this.isUpToDate = upToDate;
      this.latestVersion = latestVersion;
      this.message = message;
   }

   public IsUpToDateResponse(int count) {
      this.statusCode = count;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public boolean isUpToDate() {
      return this.isUpToDate;
   }

   public String getLatestVersion() {
      return this.latestVersion;
   }

   public String getMessage() {
      return this.message;
   }

   public boolean isSuccessFull() {
      return this.statusCode == 200;
   }
}
