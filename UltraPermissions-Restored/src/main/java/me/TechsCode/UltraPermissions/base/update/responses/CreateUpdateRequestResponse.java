package me.TechsCode.UltraPermissions.base.update.responses;

import com.google.gson.JsonObject;

public class CreateUpdateRequestResponse {
   private int statusCode;
   private String updateToken;
   private String message;

   public CreateUpdateRequestResponse(int count, String text, String message) {
      this.statusCode = count;
      this.updateToken = text;
      this.message = message;
   }

   public CreateUpdateRequestResponse(JsonObject jsonObject) {
      this.statusCode = jsonObject.has("statusCode") ? jsonObject.get("statusCode").getAsInt() : 500;
      this.updateToken = jsonObject.has("updateToken") ? jsonObject.get("updateToken").getAsString() : null;
      this.message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : null;
   }

   public CreateUpdateRequestResponse(int count) {
      this.statusCode = count;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public String getUpdateToken() {
      return this.updateToken;
   }

   public String getMessage() {
      return this.message;
   }

   public JsonObject getAsJsonObject() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("statusCode", this.statusCode);
      jsonObject.addProperty("updateToken", this.updateToken);
      jsonObject.addProperty("message", this.message);
      return jsonObject;
   }
}
