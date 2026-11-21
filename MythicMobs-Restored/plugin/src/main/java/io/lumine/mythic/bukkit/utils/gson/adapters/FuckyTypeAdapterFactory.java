package io.lumine.mythic.bukkit.utils.gson.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;

public class FuckyTypeAdapterFactory implements TypeAdapterFactory {
   public static final FuckyTypeAdapterFactory INSTANCE = new FuckyTypeAdapterFactory();

   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      Class<T> cls = (Class<T>)typeToken.getRawType();
      return cls == Optional.class ? OptionalAdapter.getInstance(gson, typeToken) : null;
   }
}
