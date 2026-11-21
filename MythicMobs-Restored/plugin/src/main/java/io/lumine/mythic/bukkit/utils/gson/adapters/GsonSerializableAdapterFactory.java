package io.lumine.mythic.bukkit.utils.gson.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import java.io.IOException;
import java.lang.reflect.Method;

public final class GsonSerializableAdapterFactory implements TypeAdapterFactory {
   public static final GsonSerializableAdapterFactory INSTANCE = new GsonSerializableAdapterFactory();

   private GsonSerializableAdapterFactory() {
   }

   public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
      Class<? super T> clazz = type.getRawType();
      final Method deserializeMethod = GsonSerializable.getDeserializeMethod(clazz);
      return (TypeAdapter<T>)(deserializeMethod == null ? null : new TypeAdapter<GsonSerializable>() {
         public void write(JsonWriter out, GsonSerializable value) {
            if (value == null) {
               gson.toJson(null, out);
            } else {
               gson.toJson(value.serialize(), out);
            }
         }

         public GsonSerializable read(JsonReader in) throws IOException {
            JsonElement element = GsonProvider.parser().parse(in);
            if (element.isJsonNull()) {
               return null;
            } else {
               try {
                  return (GsonSerializable)deserializeMethod.invoke(null, element);
               } catch (Exception e) {
                  throw new IOException(e);
               }
            }
         }
      });
   }
}
