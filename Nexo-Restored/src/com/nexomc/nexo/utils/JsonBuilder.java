package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\u000e\u001a\n\u0018\u00010\u0005j\u0004\u0018\u0001`\u0006*\u00060\u0005j\u0002`\u00062\u0006\u0010\u000f\u001a\u00020\u0010J\u001e\u0010\u0011\u001a\n\u0018\u00010\u0012j\u0004\u0018\u0001`\u0013*\u00060\u0005j\u0002`\u00062\u0006\u0010\u000f\u001a\u00020\u0010J\u001e\u0010\u0014\u001a\n\u0018\u00010\nj\u0004\u0018\u0001`\u000b*\u00060\u0005j\u0002`\u00062\u0006\u0010\u000f\u001a\u00020\u0010J\u0018\u0010\u0015\u001a\f\u0012\b\u0012\u00060\u0005j\u0002`\u00060\u0016*\u00060\nj\u0002`\u000bJ*\u0010\u0017\u001a\u00060\u0005j\u0002`\u0006*\u00060\u0005j\u0002`\u00062\u0006\u0010\u0018\u001a\u00020\u0010H\u0007b\f\b\u0019\u0012\b\b\u001a\u0012\u0004\b\b(\u001bJ4\u0010\u001c\u001a\u00060\u0005j\u0002`\u0006*\u00060\u0005j\u0002`\u00062\u0006\u0010\u0018\u001a\u00020\u00102\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u0007b\f\b\u0019\u0012\b\b\u001a\u0012\u0004\b\b(\u001bJ,\u0010\u001c\u001a\u00060\nj\u0002`\u000b*\u00060\nj\u0002`\u000b2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u0007b\f\b\u0019\u0012\b\b\u001a\u0012\u0004\b\b(\u001bJ\u0016\u0010\u001e\u001a\u00060\nj\u0002`\u000b2\n\u0010\u001f\u001a\u0006\u0012\u0002\b\u00030\u0016J\u0012\u0010 \u001a\u00060\u0005j\u0002`\u00062\u0006\u0010!\u001a\u00020\"R\u0015\u0010\u0004\u001a\u00060\u0005j\u0002`\u00068F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u0015\u0010\t\u001a\u00060\nj\u0002`\u000b8F¢\u0006\u0006\u001a\u0004\b\f\u0010\r¨\u0006#"},
   d2 = {"Lcom/nexomc/nexo/utils/JsonBuilder;", "", "<init>", "()V", "jsonObject", "Lcom/google/gson/JsonObject;", "Lcom/nexomc/nexo/utils/JsonObject;", "getJsonObject", "()Lcom/google/gson/JsonObject;", "jsonArray", "Lcom/google/gson/JsonArray;", "Lcom/nexomc/nexo/utils/JsonArray;", "getJsonArray", "()Lcom/google/gson/JsonArray;", "object", "key", "", "primitive", "Lcom/google/gson/JsonPrimitive;", "Lcom/nexomc/nexo/utils/JsonPrimitive;", "array", "objects", "", "minus", "string", "Lorg/jetbrains/annotations/Contract;", "value", "_ -> this", "plus", "any", "toJsonArray", "list", "toJsonObject", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "com.nexomc:core"}
)
public final class JsonBuilder {
   @NotNull
   public static final JsonBuilder INSTANCE = new JsonBuilder();

   private JsonBuilder() {
   }

   @NotNull
   public final JsonObject getJsonObject() {
      return new JsonObject();
   }

   @NotNull
   public final JsonArray getJsonArray() {
      return new JsonArray();
   }

   @Nullable
   public final JsonObject object(@NotNull JsonObject jsonObject, @NotNull String key) {
      JsonObject jsonObject2 = jsonObject;

      Object obj;
      Object result3;
      Object result4;
      try {
         int value = 0;
         result4 = ResultShim.ok(jsonObject2.getAsJsonObject(key));
      } catch (Throwable e) {
         result4 = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (JsonObject)(ResultShim.isFailure(result4) ? null : result4);
   }

   @Nullable
   public final JsonPrimitive primitive(@NotNull JsonObject jsonObject, @NotNull String key) {
      JsonObject jsonObject2 = jsonObject;

      Object obj;
      Object result2;
      Object result5;
      try {
         int value = 0;
         result5 = ResultShim.ok(jsonObject2.getAsJsonPrimitive(key));
      } catch (Throwable e) {
         result5 = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (JsonPrimitive)(ResultShim.isFailure(result5) ? null : result5);
   }

   @Nullable
   public final JsonArray array(@NotNull JsonObject jsonObject, @NotNull String key) {
      JsonObject jsonObject2 = jsonObject;

      Object obj;
      Object result1;
      Object result6;
      try {
         int value = 0;
         result6 = ResultShim.ok(jsonObject2.getAsJsonArray(key));
      } catch (Throwable e) {
         result6 = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (JsonArray)(ResultShim.isFailure(result6) ? null : result6);
   }

   @NotNull
   public final List<JsonObject> objects(@NotNull JsonArray jsonArray) {
      List asList = jsonArray.getAsJsonArray().asList();
      Iterable iterable = (Iterable)asList;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         if (element instanceof JsonObject) {
            destination.add(element);
         }
      }

      return (List)destination;
   }

   @Contract("_ -> this")
   @NotNull
   public final JsonObject minus(@NotNull JsonObject jsonObject, @NotNull String string) {
      int value2 = 0;
      jsonObject.remove(string);
      return jsonObject;
   }

   @Contract("_ -> this")
   @NotNull
   public final JsonObject plus(@NotNull JsonObject jsonObject, @NotNull String string, @Nullable Object any) {
      int value2 = 0;
      if (any instanceof Boolean) {
         jsonObject.addProperty(string, (Boolean)any);
      } else if (any instanceof Number) {
         jsonObject.addProperty(string, (Number)any);
      } else if (any instanceof String) {
         jsonObject.addProperty(string, (String)any);
      } else if (any instanceof Character) {
         jsonObject.addProperty(string, (Character)any);
      } else if (any instanceof JsonElement) {
         jsonObject.add(string, (JsonElement)any);
      }

      return jsonObject;
   }

   @Contract("_ -> this")
   @NotNull
   public final JsonArray plus(@NotNull JsonArray jsonArray, @Nullable Object any) {
      JsonArray jsonArray2 = jsonArray;
      int value2 = 0;
      if (any == null) {
         jsonArray.add(JsonNull.INSTANCE);
      } else if (any instanceof Boolean) {
         jsonArray.add((Boolean)any);
      } else if (any instanceof Number) {
         jsonArray.add((Number)any);
      } else if (any instanceof String) {
         jsonArray.add((String)any);
      } else if (any instanceof Character) {
         jsonArray.add((Character)any);
      } else if (any instanceof JsonArray) {
         jsonArray.addAll((JsonArray)any);
      } else if (any instanceof JsonElement) {
         jsonArray.add((JsonElement)any);
      } else if (any instanceof Collection) {
         Iterable iterable = (Iterable)any;

         for(Object element : iterable) {
            int value3 = 0;
            INSTANCE.plus(jsonArray2, element);
         }
      }

      return jsonArray;
   }

   @NotNull
   public final JsonArray toJsonArray(@NotNull List<?> list) {
      JsonArray jsonArray = this.getJsonArray();
      Iterable iterable = (Iterable)list;
      JsonArray jsonArray2 = jsonArray;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         int value = 0;
         JsonElement jsonElement;
         if (item instanceof JsonElement) {
            jsonElement = (JsonElement)item;
         } else if (item instanceof ConfigurationSection) {
            jsonElement = INSTANCE.toJsonObject((ConfigurationSection)item);
         } else if (item instanceof Map) {
            jsonElement = INSTANCE.toJsonObject(NexoYaml.Companion.toSection((Map)item));
         } else if (item instanceof List) {
            List list3 = (List)item;
            JsonBuilder jsonBuilder = INSTANCE;
            int value2 = 0;
            System.out.println(list3);
            jsonElement = jsonBuilder.toJsonArray((List)item);
         } else if (item instanceof Number) {
            jsonElement = new JsonPrimitive((Number)item);
         } else if (item instanceof String) {
            jsonElement = new JsonPrimitive((String)item);
         } else if (item instanceof Character) {
            jsonElement = new JsonPrimitive((Character)item);
         } else {
            if (!(item instanceof Boolean)) {
               throw new JsonParseException("Invalid element added to JsonArray: " + item);
            }

            jsonElement = new JsonPrimitive((Boolean)item);
         }

         destination.add(jsonElement);
      }

      List list2 = (List)destination;
      return this.plus(jsonArray2, list2);
   }

   @NotNull
   public final JsonObject toJsonObject(@NotNull ConfigurationSection section) {
      JsonObject json = this.getJsonObject();
      int value = 0;
      NexoYamlKt.forEach(section, ((a0_, a1_) -> JsonBuilder.toJsonObject$lambda$0$0(json, section, a0_, a1_)));
      return json;
   }

   private static final Unit toJsonObject$lambda$0$0(JsonObject json, ConfigurationSection section, String key, Object value) {
      if (value instanceof ConfigurationSection) {
         json.add(key, INSTANCE.toJsonObject((ConfigurationSection)value));
      } else if (value instanceof Map) {
         JsonBuilder jsonBuilder = INSTANCE;
         ConfigurationSection configurationSection = NexoYamlKt.section(section, key);
         json.add(key, jsonBuilder.toJsonObject(configurationSection));
      } else if (value instanceof List) {
         json.add(key, INSTANCE.toJsonArray((List)value));
      } else if (value instanceof Number) {
         json.addProperty(key, (Number)value);
      } else if (value instanceof String) {
         json.addProperty(key, (String)value);
      } else if (value instanceof Character) {
         json.addProperty(key, (Character)value);
      } else if (value instanceof Boolean) {
         json.addProperty(key, (Boolean)value);
      }

      return Unit.INSTANCE;
   }
}
