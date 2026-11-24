package me.TechsCode.UltraPermissions.base.translations;

import me.TechsCode.UltraPermissions.base.registry.RegistryStorable;
import com.google.gson.JsonObject;

public class TranslationRegistry extends RegistryStorable {
   private String language = "English";

   public TranslationRegistry() {
      super("translation");
   }

   @Override
   public void setState(JsonObject jsonObject) {
      this.language = jsonObject.get("language").getAsString();
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("language", this.language);
      return jsonObject;
   }

   public String getLanguage() {
      return this.language;
   }

   public void setLanguage(String text) {
      this.language = text;
      this.sync();
   }
}
