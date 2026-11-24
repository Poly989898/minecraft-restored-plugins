package me.TechsCode.UltraPermissions;

import me.TechsCode.UltraPermissions.base.registry.RegistryStorable;
import com.google.gson.JsonObject;

public class PublicRegistry extends RegistryStorable {
   private boolean defaultPermissions;
   private DefaultGroupAssignOption defaultGroupAssignOption;
   private boolean negativePermsOverride = false;

   public PublicRegistry() {
      super("public");
      this.defaultPermissions = false;
      this.defaultGroupAssignOption = DefaultGroupAssignOption.FIRST_JOIN;
   }

   @Override
   public void setState(JsonObject jsonObject) {
      this.defaultPermissions = jsonObject.get("defaultPermissions").getAsBoolean();
      this.defaultGroupAssignOption = DefaultGroupAssignOption.valueOf(jsonObject.get("defaultAssigner").getAsString());
      if (jsonObject.get("negativePermsOverride") == null) {
         jsonObject.addProperty("negativePermsOverride", this.negativePermsOverride);
      }

      this.negativePermsOverride = jsonObject.get("negativePermsOverride").getAsBoolean();
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("defaultPermissions", this.defaultPermissions);
      jsonObject.addProperty("defaultAssigner", this.defaultGroupAssignOption.name());
      jsonObject.addProperty("negativePermsOverride", this.negativePermsOverride);
      return jsonObject;
   }

   public boolean isDefaultPermissions() {
      return this.defaultPermissions;
   }

   public void setDefaultPermissions(boolean flag) {
      this.defaultPermissions = flag;
      this.sync();
   }

   public DefaultGroupAssignOption getDefaultAssignOption() {
      return this.defaultGroupAssignOption;
   }

   public void setDefaultGroupAssignOption(DefaultGroupAssignOption defaultGroupAssignOption) {
      this.defaultGroupAssignOption = defaultGroupAssignOption;
      this.sync();
   }

   public void setNegativePermsOverride(boolean flag) {
      this.negativePermsOverride = flag;
      this.sync();
   }

   public boolean isNegativePermsOverride() {
      return this.negativePermsOverride;
   }
}
