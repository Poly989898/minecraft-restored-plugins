package me.TechsCode.UltraPermissions.base;

import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.registry.RegistryStorable;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import com.google.gson.JsonObject;

public class AppearanceRegistry extends RegistryStorable {
   private String prefix;
   private boolean updaterEnabled;
   private final String defaultPrefix;
   private final int defaultGuiUpdateInterval;
   private int guiUpdateInterval;

   public AppearanceRegistry(TechPlugin<?> techPlugin) {
      super("appearance");
      this.prefix = Colors.BLUE + String.join(" ", Tools.splitCamelCase(techPlugin.getName())) + ">";
      this.updaterEnabled = true;
      this.defaultPrefix = this.prefix;
      this.guiUpdateInterval = 2;
      this.defaultGuiUpdateInterval = this.guiUpdateInterval;
   }

   @Override
   public void setState(JsonObject jsonObject) {
      this.prefix = jsonObject.get("prefix").getAsString();
      if (jsonObject.get("updaterEnabled") == null || !jsonObject.has("updaterEnabled")) {
         jsonObject.addProperty("updaterEnabled", this.updaterEnabled);
      }

      this.updaterEnabled = jsonObject.get("updaterEnabled").getAsBoolean();
      if (jsonObject.get("guiUpdateInterval") == null || !jsonObject.has("guiUpdateInterval")) {
         jsonObject.addProperty("guiUpdateInterval", this.guiUpdateInterval);
      }

      this.guiUpdateInterval = jsonObject.get("guiUpdateInterval").getAsInt();
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("prefix", this.prefix.trim());
      jsonObject.addProperty("updaterEnabled", this.updaterEnabled);
      jsonObject.addProperty("guiUpdateInterval", this.guiUpdateInterval);
      return jsonObject;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String text) {
      this.prefix = text;
      this.sync();
   }

   public boolean isPrefixModified() {
      return this.prefix.length() != 0 && !this.prefix.equals(this.defaultPrefix);
   }

   public boolean isPrefixDisabled() {
      return this.prefix.length() == 0;
   }

   public void resetPrefix() {
      this.prefix = this.defaultPrefix;
      this.sync();
   }

   public void disablePrefix() {
      this.prefix = "";
      this.sync();
   }

   public void enablePrefix() {
      this.resetPrefix();
   }

   public void toggleUpdater() {
      this.updaterEnabled = !this.updaterEnabled;
      this.sync();
   }

   public boolean isUpdaterEnabled() {
      return this.updaterEnabled;
   }

   public boolean isGuiUpdateIntervalModified() {
      return this.guiUpdateInterval != this.defaultGuiUpdateInterval;
   }

   public int getGuiUpdateInterval() {
      return this.guiUpdateInterval;
   }

   public void setGuiUpdateInterval(int count) {
      this.guiUpdateInterval = count;
      this.sync();
   }

   public void resetGuiUpdateInterval() {
      this.guiUpdateInterval = this.defaultGuiUpdateInterval;
      this.sync();
   }
}
