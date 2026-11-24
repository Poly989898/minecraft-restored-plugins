package net.Indyuce.inventory.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.commons.lang.Validate;

public class LineConfig {
   private final String key;
   private final String value;
   private final String[] args;
   private final JsonObject json;

   public LineConfig(String value) {
      this.value = value;
      if (value.contains("{") && value.contains("}")) {
         int begin = value.indexOf("{");
         int end = value.lastIndexOf("}") + 1;
         this.key = value.substring(0, begin);

         try {
            this.json = new JsonParser().parse(value.substring(begin, end)).getAsJsonObject();
         } catch (JsonParseException exception) {
            throw new IllegalArgumentException("Could not load config");
         }

         String format = value.substring(Math.min(value.length(), end + 1));
         this.args = format.isEmpty() ? new String[0] : format.split("\\ ");
      } else {
         String[] split = value.split("\\ ");
         this.key = split[0];
         this.args = split.length > 1 ? value.replace(this.key + " ", "").split("\\ ") : new String[0];
         this.json = null;
      }
   }

   public String[] args() {
      return this.args;
   }

   public String getKey() {
      return this.key;
   }

   public String getString(String path) {
      return this.json.get(path).getAsString();
   }

   public double getDouble(String path) {
      return this.json.get(path).getAsDouble();
   }

   public int getInt(String path) {
      return this.json.get(path).getAsInt();
   }

   public int getInt(String path, int def) {
      return this.json.has(path) ? this.getInt(path) : def;
   }

   public long getLong(String path) {
      return this.json.get(path).getAsLong();
   }

   public boolean getBoolean(String path) {
      return this.json.get(path).getAsBoolean();
   }

   public boolean getBoolean(String path, boolean def) {
      return this.json.has(path) ? this.getBoolean(path) : def;
   }

   public boolean contains(String path) {
      return this.json.has(path);
   }

   public void validate(String... paths) {
      for (String path : paths) {
         Validate.isTrue(this.contains(path), "Config is missing parameter '" + path + "'");
      }
   }

   public void validateArgs(int count) {
      Validate.isTrue(this.args.length >= count, "Config must have at least " + count + " parameters");
   }

   @Override
   public String toString() {
      return this.value;
   }
}
