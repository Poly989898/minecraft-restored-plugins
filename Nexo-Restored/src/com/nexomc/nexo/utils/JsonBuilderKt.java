package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Writable;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000h\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0004\n\u0000\u001a\u0012\u0010\f\u001a\n\u0018\u00010\u0001j\u0004\u0018\u0001`\r*\u00020\u000e\u001a\u0012\u0010\u000f\u001a\n\u0018\u00010\u0004j\u0004\u0018\u0001`\u0010*\u00020\u000e\u001a\u000e\u0010\u0011\u001a\u00020\u000e*\u00060\u0001j\u0002`\r\u001a\u001a\u0010\u0012\u001a\u0004\u0018\u00010\n*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002\u001a\u001f\u0010\u0015\u001a\u0004\u0018\u00010\u0016*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u0014H\u0000¢\u0006\u0002\u0010\u0017\u001a \u0010\u0015\u001a\u00020\u0016*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0016H\u0000\u001a\u001a\u0010\u0019\u001a\u0004\u0018\u00010\u0014*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u0014H\u0000\u001a \u0010\u0019\u001a\u00020\u0014*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0014H\u0000\u001a\u001f\u0010\u001a\u001a\u0004\u0018\u00010\u001b*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u0014H\u0000¢\u0006\u0002\u0010\u001c\u001a \u0010\u001a\u001a\u00020\u001b*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u001bH\u0000\u001a\u001f\u0010\u001d\u001a\u0004\u0018\u00010\u001e*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u0014H\u0000¢\u0006\u0002\u0010\u001f\u001a \u0010\u001d\u001a\u00020\u001e*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u001eH\u0000\u001a\u001f\u0010 \u001a\u0004\u0018\u00010!*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u0014H\u0000¢\u0006\u0002\u0010\"\u001a \u0010 \u001a\u00020!*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020!H\u0000\u001a \u0010#\u001a\n\u0018\u00010\u0004j\u0004\u0018\u0001`\u0010*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u0014H\u0000\u001a(\u0010#\u001a\u00060\u0004j\u0002`\u0010*\u00060\u0001j\u0002`\r2\u0006\u0010\u0013\u001a\u00020\u00142\n\u0010\u0018\u001a\u00060\u0004j\u0002`\u0010H\u0000\u001a\u001a\u0010\u0005\u001a\u00060\u0004j\u0002`$2\u0006\u0010%\u001a\u00020\u00162\u0006\u0010&\u001a\u00020'*\n\u0010\u0000\"\u00020\u00012\u00020\u0001*\n\u0010\u0002\"\u00020\u00012\u00020\u0001*\n\u0010\u0003\"\u00020\u00042\u00020\u0004*\n\u0010\u0005\"\u00020\u00042\u00020\u0004*\n\u0010\u0006\"\u00020\u00072\u00020\u0007*\n\u0010\b\"\u00020\u00072\u00020\u0007*\n\u0010\t\"\u00020\n2\u00020\n*\n\u0010\u000b\"\u00020\n2\u00020\n¨\u0006("},
   d2 = {"JsonObject", "Lcom/google/gson/JsonObject;", "NexoJsonObject", "JsonArray", "Lcom/google/gson/JsonArray;", "NexoJsonArray", "JsonPrimitive", "Lcom/google/gson/JsonPrimitive;", "NexoJsonPrimitive", "JsonElement", "Lcom/google/gson/JsonElement;", "NexoJsonElement", "toJsonObject", "Lcom/nexomc/nexo/utils/JsonObject;", "Lteam/unnamed/creative/base/Writable;", "toJsonArray", "Lcom/nexomc/nexo/utils/JsonArray;", "toWritable", "nonNull", "key", "", "int", "", "(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/Integer;", "default", "string", "float", "", "(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/Float;", "double", "", "(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/Double;", "boolean", "", "(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/Boolean;", "jsonArray", "Lcom/nexomc/nexo/utils/NexoJsonArray;", "size", "value", "", "com.nexomc:core"}
)
public final class JsonBuilderKt {
   @Nullable
   public static final JsonObject toJsonObject(@NotNull Writable writable) {
      Writable writable2 = writable;

      Object obj;
      Object result2;
      Object result3;
      try {
         int value = 0;
         result3 = ResultShim.ok(JsonParser.parseString(writable2.toUTF8String()).getAsJsonObject());
      } catch (Throwable e) {
         result3 = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (JsonObject)(ResultShim.isFailure(result3) ? null : result3);
   }

   @Nullable
   public static final JsonArray toJsonArray(@NotNull Writable writable) {
      Writable writable2 = writable;

      Object obj;
      Object result1;
      Object result4;
      try {
         int value = 0;
         result4 = ResultShim.ok(JsonParser.parseString(writable2.toUTF8String()).getAsJsonArray());
      } catch (Throwable e) {
         result4 = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (JsonArray)(ResultShim.isFailure(result4) ? null : result4);
   }

   @NotNull
   public static final Writable toWritable(@NotNull JsonObject jsonObject) {
      Writable writable = Writable.stringUtf8(jsonObject.toString());
      return writable;
   }

   private static final JsonElement nonNull(JsonObject jsonObject, String key) {
      JsonElement jsonElement = jsonObject.get(key);
      if (jsonElement != null) {
         JsonElement it = jsonElement;
         int value = 0;
         jsonElement = !it.isJsonNull() ? it : null;
      } else {
         jsonElement = null;
      }

      return jsonElement;
   }

   @Nullable
   public static final Integer int_(@NotNull JsonObject jsonObject, @NotNull String key) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      return jsonElement != null ? jsonElement.getAsInt() : null;
   }

   public static final int int_(@NotNull JsonObject jsonObject, @NotNull String key, int value) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      return jsonElement != null ? jsonElement.getAsInt() : value;
   }

   @Nullable
   public static final String string(@NotNull JsonObject jsonObject, @NotNull String key) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      return jsonElement != null ? jsonElement.getAsString() : null;
   }

   @NotNull
   public static final String string(@NotNull JsonObject jsonObject, @NotNull String key, @NotNull String str) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      String str2;
      if (jsonElement != null) {
         str2 = jsonElement.getAsString();
         if (str2 != null) {
            return str2;
         }
      }

      str2 = str;
      return str2;
   }

   @Nullable
   public static final Float float_(@NotNull JsonObject jsonObject, @NotNull String key) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      return jsonElement != null ? jsonElement.getAsFloat() : null;
   }

   public static final float float_(@NotNull JsonObject jsonObject, @NotNull String key, float value) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      return jsonElement != null ? jsonElement.getAsFloat() : value;
   }

   @Nullable
   public static final Double double_(@NotNull JsonObject jsonObject, @NotNull String key) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      return jsonElement != null ? jsonElement.getAsDouble() : null;
   }

   public static final double double_(@NotNull JsonObject jsonObject, @NotNull String key, double value) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      return jsonElement != null ? jsonElement.getAsDouble() : value;
   }

   @Nullable
   public static final Boolean boolean_(@NotNull JsonObject jsonObject, @NotNull String key) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      return jsonElement != null ? jsonElement.getAsBoolean() : null;
   }

   public static final boolean boolean_(@NotNull JsonObject jsonObject, @NotNull String key, boolean flag) {
      JsonElement jsonElement = nonNull(jsonObject, key);
      return jsonElement != null ? jsonElement.getAsBoolean() : flag;
   }

   @Nullable
   public static final JsonArray jsonArray(@NotNull JsonObject jsonObject, @NotNull String key) {
      return jsonObject.getAsJsonArray(key);
   }

   @NotNull
   public static final JsonArray jsonArray(@NotNull JsonObject jsonObject, @NotNull String key, @NotNull JsonArray jsonArray) {
      JsonArray asJsonArray = jsonObject.getAsJsonArray(key);
      if (asJsonArray == null) {
         asJsonArray = jsonArray;
      }

      return asJsonArray;
   }

   @NotNull
   public static final JsonArray NexoJsonArray(int size, @NotNull Number value) {
      JsonArray jsonArray = new JsonArray(size);
      JsonArray jsonArray2 = jsonArray;
      int value2 = 0;

      for(int i = 0; i < size; ++i) {
         int value3 = 0;
         jsonArray2.add(value);
      }

      return jsonArray;
   }
}
