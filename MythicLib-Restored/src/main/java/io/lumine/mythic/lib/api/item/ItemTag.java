package io.lumine.mythic.lib.api.item;

import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemTag {
   @NotNull
   private final String path;
   @NotNull
   private final Object value;
   static final String compression_STRING = "_ñstr";
   static final String compression_DOUBLE = "_ñdbl";
   static final String compression_BOOLEAN = "_ñbol";
   static final String compression_INTEGER = "_ñint";
   static final String compression_SLIST = "_ñlst";

   public ItemTag(@NotNull String path, @NotNull Object value) {
      this.path = path;
      this.value = value;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ItemTag)) {
         return false;
      } else {
         return !((ItemTag)obj).getValue().equals(this.getValue()) ? false : ((ItemTag)obj).getPath().equals(this.getPath());
      }
   }

   @NotNull
   public String getPath() {
      return this.path;
   }

   @NotNull
   public Object getValue() {
      return this.value;
   }

   @Nullable
   public static ItemTag getTagAtPath(@NotNull String path, @NotNull ArrayList<ItemTag> from) {
      for (ItemTag str : from) {
         if (str != null && path.equals(str.getPath())) {
            return str;
         }
      }

      return null;
   }

   @Nullable
   public static ItemTag getTagAtPath(@NotNull String path, @NotNull NBTItem from, @NotNull SupportedNBTTagValues expect) {
      if (!from.hasTag(path)) {
         return null;
      }

      switch (expect) {
         case INTEGER:
            return new ItemTag(path, from.getInteger(path));
         case STRING:
            return new ItemTag(path, from.getString(path));
         case DOUBLE:
            return new ItemTag(path, from.getDouble(path));
         default:
            return new ItemTag(path, from.getBoolean(path));
      }
   }

   @NotNull
   public static ItemTag fromStringList(@NotNull String path, @NotNull List<String> list) {
      JsonArray array = new JsonArray();
      list.forEach(array::add);
      return toItemTag(path, array);
   }

   @NotNull
   public static ArrayList<String> getStringListFromTag(@NotNull ItemTag tagThatContainsAnEncodedList) {
      JsonArray parsed = toJsonArray(tagThatContainsAnEncodedList);
      ArrayList<String> ret = new ArrayList<>();
      parsed.forEach(str -> ret.add(str.getAsString()));
      return ret;
   }

   @NotNull
   public static JsonArray toJsonArray(@NotNull ItemTag someTag) {
      JsonParser pJSON = new JsonParser();
      return pJSON.parse((String)someTag.getValue()).getAsJsonArray();
   }

   @NotNull
   public static ItemTag toItemTag(@NotNull String pathToAssign, @NotNull JsonArray someJsonArray) {
      return new ItemTag(pathToAssign, someJsonArray.toString());
   }

   @NotNull
   public static JsonArray compressTags(@NotNull ArrayList<ItemTag> tags) {
      JsonArray JsonList = new JsonArray();

      for (ItemTag t : tags) {
         JsonObject tagAsJson = new JsonObject();
         if (t.getValue() instanceof Integer) {
            tagAsJson.addProperty(t.getPath() + "_ñint", (Integer)t.getValue());
         } else if (t.getValue() instanceof Double) {
            tagAsJson.addProperty(t.getPath() + "_ñdbl", (Double)t.getValue());
         } else if (t.getValue() instanceof String) {
            tagAsJson.addProperty(t.getPath() + "_ñstr", (String)t.getValue());
         } else if (t.getValue() instanceof Boolean) {
            tagAsJson.addProperty(t.getPath() + "_ñbol", (Boolean)t.getValue());
         } else if (t.getValue() instanceof List) {
            JsonArray JSONception = new JsonArray();

            for (Object e : (List)t.getValue()) {
               if (e instanceof Number) {
                  JSONception.add(SilentNumbers.readableRounding(((Number)t.getValue()).doubleValue(), 3));
               } else if (e instanceof String) {
                  JSONception.add((String)t.getValue());
               } else if (e instanceof Boolean) {
                  JSONception.add((Boolean)t.getValue());
               }
            }

            tagAsJson.add(t.getPath() + "_ñlst", JSONception);
         }

         JsonList.add(tagAsJson);
      }

      return JsonList;
   }

   @NotNull
   public static ArrayList<ItemTag> decompressTags(@NotNull JsonArray compressedTags) {
      ArrayList<ItemTag> returningList = new ArrayList<>();

      for (JsonElement compressedTag : compressedTags) {
         if (compressedTag.isJsonObject()) {
            JsonObject cTag = compressedTag.getAsJsonObject();

            for (Entry<String, JsonElement> entry : cTag.entrySet()) {
               String rawPath = entry.getKey();
               String compressed_extension = rawPath.substring(rawPath.length() - "_ñlst".length());
               String path = rawPath.substring(0, rawPath.length() - "_ñlst".length());
               JsonElement e = entry.getValue();
               Object value = null;
               if (e.isJsonPrimitive()) {
                  JsonPrimitive p = e.getAsJsonPrimitive();
                  switch (compressed_extension) {
                     case "_ñbol":
                        value = p.getAsBoolean();
                        break;
                     case "_ñdbl":
                        value = p.getAsDouble();
                        break;
                     case "_ñint":
                        value = p.getAsInt();
                        break;
                     case "_ñstr":
                        value = p.getAsString();
                        break;
                     case "_ñlst":
                        value = p.getAsJsonArray();
                  }
               } else if (e.isJsonArray()) {
                  JsonArray a = e.getAsJsonArray();
                  ArrayList<String> ret = new ArrayList<>();

                  for (JsonElement str : a) {
                     ret.add(str.getAsString());
                  }

                  value = ret;
               }

               if (value != null) {
                  returningList.add(new ItemTag(path, value));
               }
            }
         }
      }

      return returningList;
   }
}
