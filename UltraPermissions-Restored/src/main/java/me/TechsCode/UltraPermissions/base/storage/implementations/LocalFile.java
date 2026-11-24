package me.TechsCode.UltraPermissions.base.storage.implementations;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.fileconf.FileConfiguration;
import me.TechsCode.UltraPermissions.base.storage.ReadCallback;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;
import me.TechsCode.UltraPermissions.base.storage.WriteCallback;
import org.apache.commons.codec.binary.Base64;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LocalFile extends StorageImplementation {
   private File file;
   private FileConfiguration configuration;

   public LocalFile(TechPlugin techPlugin, String text) {
      super(techPlugin, text, false);
      this.file = new File(techPlugin.getPluginFolder().getAbsolutePath() + "/" + text + ".json");
      this.configuration = new FileConfiguration(techPlugin, this.file);
   }

   @Override
   public void destroy(String text, WriteCallback writeCallback) {
      synchronized (this.configuration) {
         this.configuration.set(text, null);
         this.configuration.save();
         writeCallback.onSuccess();
      }
   }

   @Override
   public void create(String text, JsonObject jsonObject, WriteCallback writeCallback) {
      this.update(text, jsonObject, writeCallback);
   }

   @Override
   public void update(String text, JsonObject jsonObject, WriteCallback writeCallback) {
      synchronized (this.configuration) {
         String encoded = Base64.encodeBase64String(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
         this.configuration.set(text, encoded);
         this.configuration.save();
         writeCallback.onSuccess();
      }
   }

   @Override
   public void read(String text, ReadCallback readCallback) {
      synchronized (this.configuration) {
         HashMap map = new HashMap();
         JsonParser jsonParser = new JsonParser();

         for (String key : this.configuration.getKeys(false)) {
            String storedValue = this.configuration.getString(key);
            if (Base64.isBase64(storedValue)) {
               storedValue = new String(Base64.decodeBase64(storedValue), StandardCharsets.UTF_8);
            }

            JsonObject jsonObject = (JsonObject)jsonParser.parse(storedValue);
            map.put(key, jsonObject);
         }

         readCallback.onSuccess(map);
      }
   }
}
