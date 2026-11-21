package io.lumine.mythic.core.skills.placeholders.serialization;

import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywordRegistrar;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderClassSerializationRegistry {
   public static final Map<Class<?>, Function<?, String>> SerializationMap = new ConcurrentHashMap<>();
   public static final Map<Class<?>, Function<?, String>> FormattingMap = new ConcurrentHashMap<>();
   public static final Map<Class<?>, Function<String, ?>> DeserializationMap = new ConcurrentHashMap<>();
   public static final Map<Class<?>, String> NameMap = new ConcurrentHashMap<>();

   public static <T> String serialize(Class<T> clazz, T value) {
      Function<T, String> fn = (Function<T, String>)SerializationMap.get(clazz);
      if (fn == null) {
         throw new IllegalArgumentException("No serializer registered for: " + clazz.getName());
      } else {
         return fn.apply(value);
      }
   }

   public static <T> String format(Class<T> clazz, T value) {
      Function<T, String> fn = (Function<T, String>)FormattingMap.get(clazz);
      if (fn == null) {
         throw new IllegalArgumentException("No formatter registered for: " + clazz.getName());
      } else {
         return fn.apply(value);
      }
   }

   public static <T> T deserialize(Class<T> clazz, String value) {
      Function<String, T> fn = (Function<String, T>)DeserializationMap.get(clazz);
      if (fn == null) {
         throw new IllegalArgumentException("No deserializer registered for: " + clazz.getName());
      } else {
         return fn.apply(value);
      }
   }

   public static String getName(Class<?> clazz) {
      String name = NameMap.get(clazz);
      if (name == null) {
         throw new IllegalArgumentException("No name registered for: " + clazz.getName());
      } else {
         return name;
      }
   }

   public static <T> void registerPlaceholderSerialization(
      @NotNull Class<T> clazz,
      @NotNull String name,
      @NotNull Function<T, String> serializationFunction,
      @NotNull Function<T, String> formattingFunction,
      @NotNull Function<String, T> deserializationFunction
   ) {
      if (!NameMap.containsKey(clazz)) {
         NameMap.put(clazz, name);
         SerializationMap.put(clazz, serializationFunction);
         FormattingMap.put(clazz, formattingFunction);
         DeserializationMap.put(clazz, deserializationFunction);
      }
   }

   public static void registerAll(JavaPlugin plugin, String basePackage) {
      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(plugin, basePackage, MythicPlaceholderClassSerializer.class)) {
         if (PlaceholderClassSerializer.class.isAssignableFrom(clazz)) {
            registerPlaceholderSerializationClass((Class<? extends PlaceholderClassSerializer<?>>)clazz);
         }
      }

      MetaKeywords.registerConversionMetaKeywords();
   }

   public static <T> void registerPlaceholderSerializationClass(Class<? extends PlaceholderClassSerializer<?>> clazz) {
      try {
         PlaceholderClassSerializer<T> instance = PlaceholderClassSerializer.getInstance((Class<? extends PlaceholderClassSerializer<T>>)clazz);
         MythicPlaceholderClassSerializer annotation = clazz.getAnnotation(MythicPlaceholderClassSerializer.class);
         ParameterizedType type = (ParameterizedType)clazz.getGenericSuperclass();
         Type[] typeArguments = type.getActualTypeArguments();
         Class<T> targetClass = (Class<T>)MetaKeywordRegistrar.getRawClass(typeArguments[0]);
         registerPlaceholderSerialization(targetClass, annotation.name(), instance::serialize, instance::format, instance::deserialize);
      } catch (Throwable e) {
         throw new RuntimeException("Failed to register Placeholder Serialization: " + clazz.getName(), e);
      }
   }
}
