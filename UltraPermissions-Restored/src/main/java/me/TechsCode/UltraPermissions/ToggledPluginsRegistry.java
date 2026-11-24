package me.TechsCode.UltraPermissions;

import java.util.ArrayList;
import java.util.Map.Entry;
import me.TechsCode.UltraPermissions.base.registry.RegistryStorable;
import com.google.gson.JsonObject;

public class ToggledPluginsRegistry extends RegistryStorable {
   private ArrayList<String> toggledPlugins = new ArrayList<>();

   public ToggledPluginsRegistry() {
      super("toggledPlugins");
   }

   @Override
   public void setState(JsonObject jsonObject) {
      for (String toggledPlugin : this.toggledPlugins) {
         if (jsonObject.get(toggledPlugin) == null) {
            jsonObject.addProperty(toggledPlugin, false);
         }
      }

      for (Entry entry : jsonObject.entrySet()) {
         if (!this.toggledPlugins.contains(entry.getKey())) {
            this.toggledPlugins.add((String)entry.getKey());
         }
      }
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();

      for (String toggledPlugin : this.toggledPlugins) {
         jsonObject.addProperty(toggledPlugin, false);
      }

      return jsonObject;
   }

   public ArrayList<String> getToggledPlugins() {
      return this.toggledPlugins;
   }

   public void setToggledPlugins(ArrayList<String> texts) {
      this.toggledPlugins = texts;
      this.sync();
   }
}
