package io.lumine.mythic.bukkit.utils.gson.converters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@NonnullByDefault
abstract class AbstractGsonConverter<M extends Map<String, Object>, L extends List<Object>, S extends Set<Object>> implements GsonConverter {
   protected abstract AbstractGsonConverter.MapBuilder<M, String, Object> newMapBuilder();

   protected abstract AbstractGsonConverter.ListBuilder<L, Object> newListBuilder();

   protected abstract AbstractGsonConverter.SetBuilder<S, Object> newSetBuilder();

   @Override
   public M unwrapObject(JsonObject object) {
      AbstractGsonConverter.MapBuilder<M, String, Object> builder = this.newMapBuilder();

      for (Entry<String, JsonElement> e : object.entrySet()) {
         builder.put(e.getKey(), this.unwrapElement(e.getValue()));
      }

      return builder.build();
   }

   @Override
   public L unwrapArray(JsonArray array) {
      AbstractGsonConverter.ListBuilder<L, Object> builder = this.newListBuilder();

      for (JsonElement element : array) {
         builder.add(this.unwrapElement(element));
      }

      return builder.build();
   }

   @Override
   public S unwrapArrayToSet(JsonArray array) {
      AbstractGsonConverter.SetBuilder<S, Object> builder = this.newSetBuilder();

      for (JsonElement element : array) {
         builder.add(this.unwrapElement(element));
      }

      return builder.build();
   }

   @Override
   public Object unwarpPrimitive(JsonPrimitive primitive) {
      if (primitive.isBoolean()) {
         return primitive.getAsBoolean();
      } else if (primitive.isNumber()) {
         return primitive.getAsNumber();
      } else if (primitive.isString()) {
         return primitive.getAsString();
      } else {
         throw new IllegalArgumentException("Unknown primitive type: " + primitive);
      }
   }

   @Nullable
   @Override
   public Object unwrapElement(JsonElement element) {
      if (element.isJsonNull()) {
         return null;
      } else if (element.isJsonArray()) {
         return this.unwrapArray(element.getAsJsonArray());
      } else if (element.isJsonObject()) {
         return this.unwrapObject(element.getAsJsonObject());
      } else if (element.isJsonPrimitive()) {
         return this.unwarpPrimitive(element.getAsJsonPrimitive());
      } else {
         throw new IllegalArgumentException("Unknown element type: " + element);
      }
   }

   @Override
   public JsonElement wrap(Object object) {
      if (object instanceof JsonElement) {
         return (JsonElement)object;
      } else if (!(object instanceof Iterable iterable)) {
         if (object instanceof Map<?, ?> map) {
            JsonObject obj = new JsonObject();

            for (Entry<?, ?> e : map.entrySet()) {
               if (e.getKey() instanceof String) {
                  String key = (String)e.getKey();
                  obj.add(key, this.wrap(e.getValue()));
               }
            }

            return obj;
         } else if (object instanceof String) {
            return new JsonPrimitive((String)object);
         } else if (object instanceof Character) {
            return new JsonPrimitive((Character)object);
         } else if (object instanceof Boolean) {
            return new JsonPrimitive((Boolean)object);
         } else if (object instanceof Number) {
            return new JsonPrimitive((Number)object);
         } else {
            throw new IllegalArgumentException("Unable to wrap object: " + object.getClass());
         }
      } else {
         JsonArray array = new JsonArray();

         for (Object o : iterable) {
            array.add(this.wrap(o));
         }

         return array;
      }
   }

   protected interface ListBuilder<L extends List<E>, E> {
      void add(@Nullable E e);

      L build();
   }

   protected interface MapBuilder<M extends Map<K, V>, K, V> {
      void put(@Nullable K k, @Nullable V v);

      M build();
   }

   protected interface SetBuilder<S extends Set<E>, E> {
      void add(@Nullable E e);

      S build();
   }
}
