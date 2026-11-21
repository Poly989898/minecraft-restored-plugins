package com.ticxo.modelengine.api.utils.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GSONUtils {
   public static void ifPresent(JsonElement element, String key, Consumer<JsonElement> consumer) {
      if (element instanceof JsonObject object) {
         if (object.has(key)) {
            consumer.accept(object.get(key));
         }
      }
   }

   public static void ifArray(JsonElement element, String key, Consumer<JsonElement> forEach) {
      if (element instanceof JsonObject object) {
         if (object.has(key)) {
            if (object.get(key) instanceof JsonArray array) {
               array.forEach(forEach);
            }
         }
      }
   }

   public static void ifArray(JsonElement element, String key, BiConsumer<Integer, JsonElement> forEach) {
      if (element instanceof JsonObject object) {
         if (object.has(key)) {
            if (object.get(key) instanceof JsonArray array) {
               for (int i = 0; i < array.size(); i++) {
                  forEach.accept(i, array.get(i));
               }
            }
         }
      }
   }

   @Nullable
   public static <T> T get(JsonElement element, String key, Function<JsonElement, T> func) {
      if (element instanceof JsonObject object) {
         if (!object.has(key)) {
            return null;
         }

         JsonElement value = object.get(key);
         if (value.isJsonNull()) {
            return null;
         }

         try {
            return func.apply(value);
         } catch (Exception e) {
            return null;
         }
      } else {
         return null;
      }
   }

   @NotNull
   public static <T> T get(JsonElement element, String key, Function<JsonElement, T> func, @NotNull T def) {
      T val = get(element, key, func);
      return val == null ? def : val;
   }

   @NotNull
   public static <T> T get(JsonElement element, Function<JsonElement, T> func, @NotNull T def) {
      try {
         return func.apply(element);
      } catch (Exception ignored) {
         return def;
      }
   }

   public static Float[] getAsFloatArray(JsonElement jsonElement) {
      if (!jsonElement.isJsonArray()) {
         return null;
      }

      JsonArray array = jsonElement.getAsJsonArray();
      return array.asList().stream().map(JsonElement::getAsFloat).toArray(Float[]::new);
   }

   public static UUID getAsUUID(JsonElement jsonElement) {
      return UUID.fromString(jsonElement.getAsString());
   }
}
