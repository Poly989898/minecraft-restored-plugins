package io.lumine.mythic.bukkit.utils.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nullable;

@NonnullByDefault
public final class JsonBuilder {
   public static JsonBuilder.JsonObjectBuilder object(JsonObject object, boolean copy) {
      Objects.requireNonNull(object, "object");
      return (copy ? object().addAll(object, true) : new JsonBuilder.JsonObjectBuilderImpl(object));
   }

   public static JsonBuilder.JsonObjectBuilder object(JsonObject object) {
      Objects.requireNonNull(object, "object");
      return object(object, false);
   }

   public static JsonBuilder.JsonObjectBuilder object() {
      return object(new JsonObject());
   }

   public static JsonBuilder.JsonArrayBuilder array(JsonArray array, boolean copy) {
      Objects.requireNonNull(array, "array");
      return (copy ? array().addAll(array, true) : new JsonBuilder.JsonArrayBuilderImpl(array));
   }

   public static JsonBuilder.JsonArrayBuilder array(JsonArray array) {
      Objects.requireNonNull(array, "array");
      return array(array, false);
   }

   public static JsonBuilder.JsonArrayBuilder array() {
      return array(new JsonArray());
   }

   public static JsonElement primitive(@Nullable String value) {
      return (value == null ? nullValue() : new JsonPrimitive(value));
   }

   public static JsonElement primitive(@Nullable Number value) {
      return (value == null ? nullValue() : new JsonPrimitive(value));
   }

   public static JsonElement primitive(@Nullable Boolean value) {
      return (value == null ? nullValue() : new JsonPrimitive(value));
   }

   public static JsonElement primitive(@Nullable Character value) {
      return (value == null ? nullValue() : new JsonPrimitive(value));
   }

   public static JsonNull nullValue() {
      return JsonNull.INSTANCE;
   }

   public static JsonPrimitive primitiveNonNull(String value) {
      Objects.requireNonNull(value, "value");
      return new JsonPrimitive(value);
   }

   public static JsonPrimitive primitiveNonNull(Number value) {
      Objects.requireNonNull(value, "value");
      return new JsonPrimitive(value);
   }

   public static JsonPrimitive primitiveNonNull(Boolean value) {
      Objects.requireNonNull(value, "value");
      return new JsonPrimitive(value);
   }

   public static JsonPrimitive primitiveNonNull(Character value) {
      Objects.requireNonNull(value, "value");
      return new JsonPrimitive(value);
   }

   public static <T> Collector<T, JsonBuilder.JsonObjectBuilder, JsonObject> collectToObject(
      Function<? super T, String> keyMapper, Function<? super T, JsonElement> valueMapper
   ) {
      return Collector.of(
         JsonBuilder::object, (r, t) -> r.add(keyMapper.apply(t), valueMapper.apply(t)), (l, r) -> l.addAll(r.build()), JsonBuilder.JsonObjectBuilder::build
      );
   }

   public static <T> Collector<T, JsonBuilder.JsonArrayBuilder, JsonArray> collectToArray(Function<? super T, JsonElement> valueMapper) {
      return Collector.of(JsonBuilder::array, (r, t) -> r.add(valueMapper.apply(t)), (l, r) -> l.addAll(r.build()), JsonBuilder.JsonArrayBuilder::build);
   }

   public static Collector<JsonElement, JsonBuilder.JsonArrayBuilder, JsonArray> collectToArray() {
      return Collector.of(JsonBuilder::array, JsonBuilder.JsonArrayBuilder::add, (l, r) -> l.addAll(r.build()), JsonBuilder.JsonArrayBuilder::build);
   }

   public static Collector<GsonSerializable, JsonBuilder.JsonArrayBuilder, JsonArray> collectSerializablesToArray() {
      return Collector.of(JsonBuilder::array, JsonBuilder.JsonArrayBuilder::add, (l, r) -> l.addAll(r.build()), JsonBuilder.JsonArrayBuilder::build);
   }

   private JsonBuilder() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   public interface JsonArrayBuilder extends Consumer<JsonElement> {
      default void accept(JsonElement value) {
         this.add(value);
      }

      JsonBuilder.JsonArrayBuilder add(@Nullable JsonElement jsonElement, boolean flag);

      default JsonBuilder.JsonArrayBuilder add(@Nullable JsonElement value) {
         return this.add(value, false);
      }

      default JsonBuilder.JsonArrayBuilder add(@Nullable GsonSerializable serializable) {
         return serializable == null ? this.add(JsonBuilder.nullValue()) : this.add(serializable.serialize());
      }

      default JsonBuilder.JsonArrayBuilder add(@Nullable String value) {
         return this.add(JsonBuilder.primitive(value));
      }

      default JsonBuilder.JsonArrayBuilder add(@Nullable Number value) {
         return this.add(JsonBuilder.primitive(value));
      }

      default JsonBuilder.JsonArrayBuilder add(@Nullable Boolean value) {
         return this.add(JsonBuilder.primitive(value));
      }

      default JsonBuilder.JsonArrayBuilder add(@Nullable Character value) {
         return this.add(JsonBuilder.primitive(value));
      }

      default <T extends JsonElement> JsonBuilder.JsonArrayBuilder addAll(Iterable<T> iterable, boolean copy) {
         Objects.requireNonNull(iterable, "iterable");

         for (T e : iterable) {
            this.add(e, copy);
         }

         return this;
      }

      default <T extends JsonElement> JsonBuilder.JsonArrayBuilder addAll(Iterable<T> iterable) {
         return this.addAll(iterable, false);
      }

      default <T extends JsonElement> JsonBuilder.JsonArrayBuilder addAll(Stream<T> stream, boolean copy) {
         Objects.requireNonNull(stream, "iterable");
         stream.forEach(e -> this.add(e, copy));
         return this;
      }

      default <T extends JsonElement> JsonBuilder.JsonArrayBuilder addAll(Stream<T> stream) {
         return this.addAll(stream, false);
      }

      default <T extends GsonSerializable> JsonBuilder.JsonArrayBuilder addSerializables(Iterable<T> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (T e : iterable) {
            this.add(e);
         }

         return this;
      }

      default JsonBuilder.JsonArrayBuilder addStrings(Iterable<String> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (String e : iterable) {
            this.add(e);
         }

         return this;
      }

      default <T extends Number> JsonBuilder.JsonArrayBuilder addNumbers(Iterable<T> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (T e : iterable) {
            this.add(e);
         }

         return this;
      }

      default JsonBuilder.JsonArrayBuilder addBooleans(Iterable<Boolean> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Boolean e : iterable) {
            this.add(e);
         }

         return this;
      }

      default JsonBuilder.JsonArrayBuilder addCharacters(Iterable<Character> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Character e : iterable) {
            this.add(e);
         }

         return this;
      }

      JsonArray build();
   }

   private static final class JsonArrayBuilderImpl implements JsonBuilder.JsonArrayBuilder {
      private final JsonArray handle;

      private JsonArrayBuilderImpl(JsonArray handle) {
         this.handle = handle;
      }

      @Override
      public JsonBuilder.JsonArrayBuilder add(@Nullable JsonElement value, boolean copy) {
         if (value == null) {
            value = JsonBuilder.nullValue();
         }

         if (copy && value.isJsonObject()) {
            this.handle.add(JsonBuilder.object(value.getAsJsonObject(), true).build());
         } else if (copy && value.isJsonArray()) {
            this.handle.add(JsonBuilder.array(value.getAsJsonArray(), true).build());
         } else {
            this.handle.add(value);
         }

         return this;
      }

      @Override
      public JsonArray build() {
         return this.handle;
      }
   }

   public interface JsonObjectBuilder extends BiConsumer<String, JsonElement>, Consumer<Entry<String, JsonElement>> {
      default void accept(Entry<String, JsonElement> entry) {
         Objects.requireNonNull(entry, "entry");
         this.add(entry.getKey(), entry.getValue());
      }

      default void accept(String property, JsonElement value) {
         this.add(property, value);
      }

      JsonBuilder.JsonObjectBuilder add(String str, @Nullable JsonElement jsonElement, boolean flag);

      default JsonBuilder.JsonObjectBuilder add(String property, @Nullable JsonElement value) {
         return this.add(property, value, false);
      }

      default JsonBuilder.JsonObjectBuilder add(String property, @Nullable GsonSerializable serializable) {
         return serializable == null ? this.add(property, JsonBuilder.nullValue()) : this.add(property, serializable.serialize());
      }

      default JsonBuilder.JsonObjectBuilder add(String property, @Nullable String value) {
         return this.add(property, JsonBuilder.primitive(value));
      }

      default JsonBuilder.JsonObjectBuilder add(String property, @Nullable Number value) {
         return this.add(property, JsonBuilder.primitive(value));
      }

      default JsonBuilder.JsonObjectBuilder add(String property, @Nullable Boolean value) {
         return this.add(property, JsonBuilder.primitive(value));
      }

      default JsonBuilder.JsonObjectBuilder add(String property, @Nullable Character value) {
         return this.add(property, JsonBuilder.primitive(value));
      }

      JsonBuilder.JsonObjectBuilder addIfAbsent(String str, @Nullable JsonElement jsonElement, boolean flag);

      default JsonBuilder.JsonObjectBuilder addIfAbsent(String property, @Nullable JsonElement value) {
         return this.addIfAbsent(property, value, false);
      }

      default JsonBuilder.JsonObjectBuilder addIfAbsent(String property, @Nullable GsonSerializable serializable) {
         return serializable == null ? this.addIfAbsent(property, JsonBuilder.nullValue()) : this.addIfAbsent(property, serializable.serialize());
      }

      default JsonBuilder.JsonObjectBuilder addIfAbsent(String property, @Nullable String value) {
         return this.addIfAbsent(property, JsonBuilder.primitive(value));
      }

      default JsonBuilder.JsonObjectBuilder addIfAbsent(String property, @Nullable Number value) {
         return this.addIfAbsent(property, JsonBuilder.primitive(value));
      }

      default JsonBuilder.JsonObjectBuilder addIfAbsent(String property, @Nullable Boolean value) {
         return this.addIfAbsent(property, JsonBuilder.primitive(value));
      }

      default JsonBuilder.JsonObjectBuilder addIfAbsent(String property, @Nullable Character value) {
         return this.addIfAbsent(property, JsonBuilder.primitive(value));
      }

      default <T extends JsonElement> JsonBuilder.JsonObjectBuilder addAll(Iterable<Entry<String, T>> iterable, boolean deepCopy) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, T> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.add(e.getKey(), e.getValue(), deepCopy);
            }
         }

         return this;
      }

      default <T extends JsonElement> JsonBuilder.JsonObjectBuilder addAll(Iterable<Entry<String, T>> iterable) {
         return this.addAll(iterable, false);
      }

      default <T extends JsonElement> JsonBuilder.JsonObjectBuilder addAll(Stream<Entry<String, T>> stream, boolean deepCopy) {
         Objects.requireNonNull(stream, "stream");
         stream.forEach(e -> {
            if (e != null && e.getKey() != null) {
               this.add(e.getKey(), e.getValue(), deepCopy);
            }
         });
         return this;
      }

      default <T extends JsonElement> JsonBuilder.JsonObjectBuilder addAll(Stream<Entry<String, T>> stream) {
         return this.addAll(stream, false);
      }

      default JsonBuilder.JsonObjectBuilder addAll(JsonObject object, boolean deepCopy) {
         Objects.requireNonNull(object, "object");
         return this.addAll(object.entrySet(), deepCopy);
      }

      default JsonBuilder.JsonObjectBuilder addAll(JsonObject object) {
         return this.addAll(object, false);
      }

      default <T extends GsonSerializable> JsonBuilder.JsonObjectBuilder addAllSerializables(Iterable<Entry<String, T>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, T> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.add(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      default JsonBuilder.JsonObjectBuilder addAllStrings(Iterable<Entry<String, String>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, String> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.add(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      default <T extends Number> JsonBuilder.JsonObjectBuilder addAllNumbers(Iterable<Entry<String, T>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, T> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.add(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      default JsonBuilder.JsonObjectBuilder addAllBooleans(Iterable<Entry<String, Boolean>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, Boolean> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.add(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      default JsonBuilder.JsonObjectBuilder addAllCharacters(Iterable<Entry<String, Character>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, Character> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.add(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      default <T extends JsonElement> JsonBuilder.JsonObjectBuilder addAllIfAbsent(Iterable<Entry<String, T>> iterable, boolean deepCopy) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, T> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.addIfAbsent(e.getKey(), e.getValue(), deepCopy);
            }
         }

         return this;
      }

      default <T extends JsonElement> JsonBuilder.JsonObjectBuilder addAllIfAbsent(Iterable<Entry<String, T>> iterable) {
         return this.addAllIfAbsent(iterable, false);
      }

      default <T extends JsonElement> JsonBuilder.JsonObjectBuilder addAllIfAbsent(Stream<Entry<String, T>> stream, boolean deepCopy) {
         Objects.requireNonNull(stream, "stream");
         stream.forEach(e -> {
            if (e != null && e.getKey() != null) {
               this.addIfAbsent(e.getKey(), e.getValue(), deepCopy);
            }
         });
         return this;
      }

      default <T extends JsonElement> JsonBuilder.JsonObjectBuilder addAllIfAbsent(Stream<Entry<String, T>> stream) {
         return this.addAllIfAbsent(stream, false);
      }

      default JsonBuilder.JsonObjectBuilder addAllIfAbsent(JsonObject object, boolean deepCopy) {
         Objects.requireNonNull(object, "object");
         return this.addAllIfAbsent(object.entrySet(), deepCopy);
      }

      default JsonBuilder.JsonObjectBuilder addAllIfAbsent(JsonObject object) {
         return this.addAllIfAbsent(object, false);
      }

      default <T extends GsonSerializable> JsonBuilder.JsonObjectBuilder addAllSerializablesIfAbsent(Iterable<Entry<String, T>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, T> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.addIfAbsent(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      default JsonBuilder.JsonObjectBuilder addAllStringsIfAbsent(Iterable<Entry<String, String>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, String> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.addIfAbsent(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      default <T extends Number> JsonBuilder.JsonObjectBuilder addAllNumbersIfAbsent(Iterable<Entry<String, T>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, T> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.addIfAbsent(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      default JsonBuilder.JsonObjectBuilder addAllBooleansIfAbsent(Iterable<Entry<String, Boolean>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, Boolean> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.addIfAbsent(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      default JsonBuilder.JsonObjectBuilder addAllCharactersIfAbsent(Iterable<Entry<String, Character>> iterable) {
         Objects.requireNonNull(iterable, "iterable");

         for (Entry<String, Character> e : iterable) {
            if (e != null && e.getKey() != null) {
               this.addIfAbsent(e.getKey(), e.getValue());
            }
         }

         return this;
      }

      JsonObject build();
   }

   private static final class JsonObjectBuilderImpl implements JsonBuilder.JsonObjectBuilder {
      private final JsonObject handle;

      private JsonObjectBuilderImpl(JsonObject handle) {
         this.handle = handle;
      }

      @Override
      public JsonBuilder.JsonObjectBuilder add(String property, @Nullable JsonElement value, boolean copy) {
         Objects.requireNonNull(property, "property");
         if (value == null) {
            value = JsonBuilder.nullValue();
         }

         if (copy && value.isJsonObject()) {
            this.handle.add(property, JsonBuilder.object(value.getAsJsonObject(), true).build());
         } else if (copy && value.isJsonArray()) {
            this.handle.add(property, JsonBuilder.array(value.getAsJsonArray(), true).build());
         } else {
            this.handle.add(property, value);
         }

         return this;
      }

      @Override
      public JsonBuilder.JsonObjectBuilder addIfAbsent(String property, @Nullable JsonElement value, boolean copy) {
         Objects.requireNonNull(property, "property");
         return (this.handle.has(property) ? this : this.add(property, value, copy));
      }

      @Override
      public JsonObject build() {
         return this.handle;
      }
   }
}
