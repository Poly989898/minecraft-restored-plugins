package me.TechsCode.UltraPermissions.base.update;

import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UpdateRequest {
   private final String updateToken;
   private final UpdateRequestStatus status;
   private final String pluginName;
   private final String currentVersion;
   private final String updateTo;
   private final ArrayList<String> allowedPlugins = new ArrayList<>();
   private final boolean hasBetaAccess;
   private final String updatedAt;
   private final String createdAt;

   public UpdateRequest(JsonObject jsonObject) {
      this.updateToken = jsonObject.has("update_token") ? jsonObject.get("update_token").getAsString() : null;
      this.status = jsonObject.has("status") ? UpdateRequestStatus.fromString(jsonObject.get("status").getAsString()) : null;
      this.pluginName = jsonObject.has("plugin_name") ? jsonObject.get("plugin_name").getAsString() : null;
      this.currentVersion = jsonObject.has("current_version") ? jsonObject.get("current_version").getAsString() : null;
      this.updateTo = jsonObject.has("update_to") ? jsonObject.get("update_to").getAsString() : null;
      JsonArray jsonArray = jsonObject.has("allowed_plugins") ? jsonObject.get("allowed_plugins").getAsJsonArray() : null;
      if (jsonArray != null) {
         jsonArray.forEach(jsonElement -> this.allowedPlugins.add(jsonElement.getAsString()));
      }

      this.hasBetaAccess = jsonObject.has("has_beta_access") ? jsonObject.get("has_beta_access").getAsBoolean() : null;
      this.updatedAt = jsonObject.has("updated_at") ? jsonObject.get("updated_at").getAsString() : null;
      this.createdAt = jsonObject.has("created_at") ? jsonObject.get("created_at").getAsString() : null;
   }

   public String getUpdateToken() {
      return this.updateToken;
   }

   public UpdateRequestStatus getStatus() {
      return this.status;
   }

   public String getPluginName() {
      return this.pluginName;
   }

   public String getCurrentVersion() {
      return this.currentVersion;
   }

   public String getUpdateTo() {
      return this.updateTo;
   }

   public ArrayList<String> getAllowedPlugins() {
      return this.allowedPlugins;
   }

   public boolean hasBetaAccess() {
      return this.hasBetaAccess;
   }

   public String getUpdatedAt() {
      return this.updatedAt;
   }

   public String getCreatedAt() {
      return this.createdAt;
   }

   public String getUpdateToVersionName() {
      return this.updateTo.split("_")[0];
   }
}
