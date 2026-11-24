package me.TechsCode.UltraPermissions.base.update.responses;

import me.TechsCode.UltraPermissions.base.update.UpdateRequest;

public class UpdateUpdateRequestResponse {
   private final int statusCode;
   private UpdateRequest updateRequest;
   private String message;

   public UpdateUpdateRequestResponse(int count, UpdateRequest updateRequest, String text) {
      this.statusCode = count;
      this.updateRequest = updateRequest;
      this.message = text;
   }

   public UpdateUpdateRequestResponse(int count) {
      this.statusCode = count;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public UpdateRequest getUpdateRequest() {
      return this.updateRequest;
   }

   public String getMessage() {
      return this.message;
   }
}
