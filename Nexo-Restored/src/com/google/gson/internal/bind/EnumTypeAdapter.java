package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
   static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         Class<? super T> rawType = typeToken.getRawType();
         if (Enum.class.isAssignableFrom(rawType) && rawType != Enum.class) {
            if (!rawType.isEnum()) {
               rawType = rawType.getSuperclass();
            }

            TypeAdapter<T> adapter = new EnumTypeAdapter<T>(rawType);
            return adapter;
         } else {
            return null;
         }
      }
   };
   private final Map<String, T> nameToConstant;
   private final Map<String, T> stringToConstant;
   private final Map<T, String> constantToName;

   private EnumTypeAdapter(Class<T> classOfT) {
      this.nameToConstant = new HashMap();
      this.stringToConstant = new HashMap();
      this.constantToName = new HashMap();

      try {
         Field[] fields = classOfT.getDeclaredFields();
         int constantCount = 0;

         for(Field f : fields) {
            if (f.isEnumConstant()) {
               fields[constantCount++] = f;
            }
         }

         fields = (Field[])Arrays.copyOf(fields, constantCount);
         AccessibleObject.setAccessible(fields, true);

         for(Field constantField : fields) {
            T constant = (T)(constantField.get((Object)null));
            String name = constant.name();
            String toStringVal = constant.toString();
            SerializedName annotation = (SerializedName)constantField.getAnnotation(SerializedName.class);
            if (annotation != null) {
               name = annotation.value();

               for(String alternate : annotation.alternate()) {
                  this.nameToConstant.put(alternate, constant);
               }
            }

            this.nameToConstant.put(name, constant);
            this.stringToConstant.put(toStringVal, constant);
            this.constantToName.put(constant, name);
         }

      } catch (IllegalAccessException e) {
         throw new AssertionError(e);
      }
   }

   public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         String key = in.nextString();
         T constant = (T)(this.nameToConstant.get(key));
         return (T)(constant == null ? (Enum)this.stringToConstant.get(key) : constant);
      }
   }

   public void write(JsonWriter out, T value) throws IOException {
      out.value(value == null ? null : (String)this.constantToName.get(value));
   }
}
