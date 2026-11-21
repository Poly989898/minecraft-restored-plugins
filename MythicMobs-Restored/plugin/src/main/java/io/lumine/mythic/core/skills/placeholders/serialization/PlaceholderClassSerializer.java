package io.lumine.mythic.core.skills.placeholders.serialization;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public abstract class PlaceholderClassSerializer<T> {
   private static final Map<Class<?>, PlaceholderClassSerializer<?>> instances = new ConcurrentHashMap<>();

   public static <T> PlaceholderClassSerializer<T> getInstance(Class<? extends PlaceholderClassSerializer<T>> clazz) {
      return (PlaceholderClassSerializer<T>)instances.computeIfAbsent(clazz, k -> {
         try {
            return (PlaceholderClassSerializer<?>)k.getDeclaredConstructor().newInstance();
         } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate serializer: " + k.getName(), e);
         }
      });
   }

   @NotNull
   public abstract String serialize(@NotNull T t);

   @NotNull
   public String format(@NotNull T object) {
      return this.serialize(object);
   }

   @NotNull
   public abstract T deserialize(@NotNull String str);
}
