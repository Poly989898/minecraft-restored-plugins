package io.lumine.mythic.bukkit.utils.gson.converters;

import javax.annotation.Nonnull;

public class GsonConverters {
   @Nonnull
   public static final GsonConverter IMMUTABLE = ImmutableGsonConverter.INSTANCE;
   @Nonnull
   public static final GsonConverter MUTABLE = MutableGsonConverter.INSTANCE;

   private GsonConverters() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
