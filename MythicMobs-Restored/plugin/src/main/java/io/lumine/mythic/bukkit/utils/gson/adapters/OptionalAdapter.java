package io.lumine.mythic.bukkit.utils.gson.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

class OptionalAdapter<T> extends TypeAdapter<Optional<T>> {
   private final TypeAdapter<T> delegate;

   public OptionalAdapter(TypeAdapter<T> delegate) {
      this.delegate = delegate;
   }

   public void write(JsonWriter out, Optional<T> value) throws IOException {
      if (value == null) {
         out.nullValue();
      } else if (!value.isPresent()) {
         out.nullValue();
      } else {
         this.delegate.write(out, value.get());
      }
   }

   public Optional<T> read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return Optional.empty();
      } else {
         return Optional.of(this.delegate.read(in));
      }
   }

   public static OptionalAdapter getInstance(Gson gson, TypeToken typeToken) {
      Type type = typeToken.getType();
      TypeAdapter delegate;
      if (type instanceof ParameterizedType) {
         Type innerType = ((ParameterizedType)type).getActualTypeArguments()[0];
         delegate = gson.getAdapter(TypeToken.get(innerType));
      } else {
         if (!(type instanceof Class)) {
            throw new JsonIOException("Unexpected type type:" + type.getClass());
         }

         delegate = gson.getAdapter(Object.class);
      }

      return new OptionalAdapter(delegate);
   }
}
