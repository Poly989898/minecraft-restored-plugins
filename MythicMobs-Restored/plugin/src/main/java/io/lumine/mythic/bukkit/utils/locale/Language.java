package io.lumine.mythic.bukkit.utils.locale;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Language {
   private final LuminePlugin plugin;
   private final String folder;
   private final String languageFile;
   private String language;
   private Map<String, String> languageData = new HashMap<>();
   private final Gson gson = GsonProvider.prettyPrinting();

   public Language(LuminePlugin plugin, String folder, String languageFile) {
      this.plugin = plugin;
      this.folder = folder;
      this.languageFile = languageFile;
   }

   public void setLanguage(String language) {
      this.language = language;
      this.reload();
   }

   public void loadLanguageFile() {
      String filePath = String.format("%s/%s/%s.json", this.folder, this.language, this.languageFile);
      File file = new File(this.plugin.getDataFolder(), filePath);
      if (!file.exists()) {
         file.getParentFile().mkdirs();
         this.languageData = new HashMap<>();
         this.saveLanguageFile();
      } else {
         try {
            block: {
               try (Reader reader = new FileReader(file)) {
                  JsonObject jsonObject = this.gson.fromJson(reader, JsonObject.class);
                  if (jsonObject != null) {
                     this.languageData.clear();

                     for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                        this.languageData.put(entry.getKey(), entry.getValue().getAsString());
                     }
                     break block;
                  }

                  Log.error("Failed to load Language File: {0}", file.getAbsolutePath());
               }

               return;
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public void reload() {
      this.loadLanguageFile();
   }

   public String getString(String key, String defaultValue, Object... args) {
      String message = this.languageData.get(key);
      if (message == null) {
         message = defaultValue;
         this.languageData.put(key, defaultValue);
         this.saveLanguageFile();
      }

      for (int i = 0; i < args.length; i++) {
         message = message.replace("{" + i + "}", args[i].toString());
      }

      return message;
   }

   private void saveLanguageFile() {
      String filePath = String.format("%s/%s/%s.json", this.folder, this.language, this.languageFile);
      File file = new File(this.plugin.getDataFolder(), filePath);

      try (Writer writer = new FileWriter(file)) {
         JsonObject jsonObject = new JsonObject();
         this.languageData.entrySet().stream().sorted(Entry.comparingByKey()).forEachOrdered(entry -> jsonObject.addProperty(entry.getKey(), entry.getValue()));
         this.gson.toJson(jsonObject, writer);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
