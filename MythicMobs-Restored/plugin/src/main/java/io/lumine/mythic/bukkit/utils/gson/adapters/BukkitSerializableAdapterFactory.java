package io.lumine.mythic.bukkit.utils.gson.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public final class BukkitSerializableAdapterFactory implements TypeAdapterFactory {
   public static final BukkitSerializableAdapterFactory INSTANCE = new BukkitSerializableAdapterFactory();

   private BukkitSerializableAdapterFactory() {
   }

   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      Class<? super T> clazz = type.getRawType();
      return !ConfigurationSerializable.class.isAssignableFrom(clazz) ? null : (TypeAdapter<T>)(TypeAdapter)new BukkitSerializableAdapterFactory.Adapter(gson);
   }

   private static final class Adapter extends TypeAdapter<ConfigurationSerializable> {
      private static final Type RAW_OUTPUT_TYPE = (new TypeToken<Map<String, Object>>() {}).getType();
      private final Gson gson;

      private Adapter(Gson gson) {
         this.gson = gson;
      }

      public void write(JsonWriter out, ConfigurationSerializable value) {
         Map<String, Object> serialized = value.serialize();
         Map<String, Object> map = new LinkedHashMap<>(serialized.size() + 1);
         map.put("==", ConfigurationSerialization.getAlias(value.getClass()));
         map.putAll(serialized);
         this.gson.toJson(map, RAW_OUTPUT_TYPE, out);
      }

      public ConfigurationSerializable read(JsonReader in) {
         Map<String, Object> map = (Map<String, Object>)this.gson.fromJson(in, RAW_OUTPUT_TYPE);
         this.deserializeChildren(map);
         return ConfigurationSerialization.deserializeObject(map);
      }

      private void deserializeChildren(Map<String, Object> map) {
         for (Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
               try {
                  Map<String, Object> value = (Map<String, Object>)entry.getValue();
                  this.deserializeChildren(value);
                  if (value.containsKey("==")) {
                     entry.setValue(ConfigurationSerialization.deserializeObject(value));
                  }
               } catch (Exception e) {
               }
            }

            if (entry.getValue() instanceof Number) {
               double doubleVal = ((Number)entry.getValue()).doubleValue();
               int intVal = (int)doubleVal;
               long longVal = (long)doubleVal;
               if (intVal == doubleVal) {
                  entry.setValue(intVal);
               } else if (longVal == doubleVal) {
                  entry.setValue(longVal);
               } else {
                  entry.setValue(doubleVal);
               }
            }
         }
      }
   }
}
