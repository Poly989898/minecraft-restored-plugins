package io.lumine.mythic.lib.util.configobject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JsonWrapper implements ConfigObject {
   protected final String key;
   protected final JsonObject object;

   public JsonWrapper(String key, JsonObject object) {
      this.key = key;
      this.object = object;
   }

   protected JsonWrapper(String value) {
      if (value.contains("{") && value.contains("}")) {
         int begin = value.indexOf("{");
         int end = value.lastIndexOf("}") + 1;
         this.object = JsonParser.parseString(value.substring(begin, end)).getAsJsonObject();
         this.key = this.nullify(value.substring(0, begin));
      } else {
         this.object = new JsonObject();
         this.key = null;
      }
   }

   @Nullable
   private String nullify(@Nullable String str) {
      return str != null && !str.isEmpty() ? str : null;
   }

   @Override
   public String getString(String key) {
      return this.object.get(key).getAsString();
   }

   @Override
   public String getString(String key, String defaultValue) {
      return this.object.has(key) ? this.getString(key) : defaultValue;
   }

   @Override
   public double getDouble(String key) {
      return this.object.get(key).getAsDouble();
   }

   @Override
   public double getDouble(String key, double defaultValue) {
      return this.object.has(key) ? this.getDouble(key) : defaultValue;
   }

   @Override
   public int getInt(String key) {
      return this.object.get(key).getAsInt();
   }

   @Override
   public int getInt(String key, int defaultValue) {
      return this.object.has(key) ? this.getInt(key) : defaultValue;
   }

   @NotNull
   @Override
   public ConfigObject adaptObject(String key) {
      JsonElement found = this.object.get(key);
      JsonObject loadFrom;
      if (found instanceof JsonObject) {
         loadFrom = found.getAsJsonObject();
      } else {
         if (!(found instanceof JsonPrimitive)) {
            throw new IllegalArgumentException("Expecting either a string or object");
         }

         loadFrom = new JsonObject();
         loadFrom.addProperty("type", found.getAsString());
      }

      return new JsonWrapper(key, loadFrom);
   }

   @Override
   public boolean getBoolean(String key) {
      return this.object.get(key).getAsBoolean();
   }

   @Override
   public boolean getBoolean(String key, boolean defaultValue) {
      return this.object.has(key) ? this.getBoolean(key) : defaultValue;
   }

   @NotNull
   @Override
   public ConfigObject getObject(String key) {
      return new JsonWrapper(key, this.object.getAsJsonObject(key));
   }

   @Override
   public boolean contains(String key) {
      return this.object.has(key);
   }

   @NotNull
   @Override
   public Set<String> getKeys() {
      return this.object.keySet();
   }

   @Override
   public String getKey() {
      return this.key;
   }
}
