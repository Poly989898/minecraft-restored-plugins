package me.TechsCode.UltraPermissions.base.update.responses;

import com.google.gson.JsonObject;

public class DownloadErrorResponse {
   private final String status;
   private final int code;
   private final String message;

   public DownloadErrorResponse(String text, int count, String message) {
      this.status = text;
      this.code = count;
      this.message = message;
   }

   public DownloadErrorResponse(JsonObject jsonObject) {
      this.status = jsonObject.has("status") ? jsonObject.get("status").getAsString() : null;
      this.code = jsonObject.has("code") ? jsonObject.get("code").getAsInt() : 500;
      this.message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : null;
   }

   public String getMessage() {
      return this.message;
   }

   public int getCode() {
      return this.code;
   }

   public String getStatus() {
      return this.status;
   }

   public JsonObject getAsJsonObject() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("status", this.status);
      jsonObject.addProperty("code", this.code);
      jsonObject.addProperty("message", this.message);
      return jsonObject;
   }
}
