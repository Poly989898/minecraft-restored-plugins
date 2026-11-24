package me.TechsCode.UltraPermissions.base.update.responses;

import com.google.gson.JsonObject;

public class GetPluginsResponse {
   private int statusCode;
   private JsonObject data;
   private String message;

   public GetPluginsResponse(int count, JsonObject jsonObject, String text) {
      this.statusCode = count;
      this.data = jsonObject;
      this.message = text;
   }

   public GetPluginsResponse(int count) {
      this.statusCode = count;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public JsonObject getData() {
      return this.data;
   }

   public String getMessage() {
      return this.message;
   }
}
