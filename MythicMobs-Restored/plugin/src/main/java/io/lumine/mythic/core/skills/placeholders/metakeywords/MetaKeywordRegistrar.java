package io.lumine.mythic.core.skills.placeholders.metakeywords;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.Map.Entry;
import org.bukkit.plugin.java.JavaPlugin;

public final class MetaKeywordRegistrar {
   public static void registerAll(String basePackage) {
      registerAll(MythicBukkit.inst(), basePackage);
   }

   public static void registerAll(JavaPlugin plugin, String basePackage) {
      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(plugin, basePackage, MythicMetaKeyword.class)) {
         if (AbstractMetaKeyword.class.isAssignableFrom(clazz)) {
            registerKeywordClass((Class<? extends AbstractMetaKeyword<?, ?>>)clazz);
         }
      }
   }

   private static <T, K> void registerKeywordClass(Class<? extends AbstractMetaKeyword<?, ?>> rawClass) {
      try {
         AbstractMetaKeyword<T, K> instance = (AbstractMetaKeyword<T, K>)rawClass.getDeclaredConstructor().newInstance();
         MythicMetaKeyword annotation = rawClass.getAnnotation(MythicMetaKeyword.class);
         ParameterizedType type = (ParameterizedType)rawClass.getGenericSuperclass();
         Type[] typeArguments = type.getActualTypeArguments();
         String[] aliases = annotation.aliases();
         List<String> keys = new ArrayList<>(aliases.length + 1);
         keys.add(annotation.name());
         Collections.addAll(keys, aliases);
         Class<T> fromClass = (Class<T>)getRawClass(typeArguments[0]);
         Class<K> toClass = (Class<K>)getRawClass(typeArguments[1]);
         Collection<MetaKeywords.MetaKeywordsAttribute> attributes = instance.getAttributes();
         OptionalInt requiredArgs = attributes.stream().mapToInt(arg -> arg.index).max();

         for (String name : keys) {
            Entry<String, MetaKeywords.MetaKeywordsDefinition<T, K>> entry = MetaKeywords.add(
               name, requiredArgs.isPresent() ? requiredArgs.getAsInt() : 0, attributes, instance::extract, toClass
            );
            MetaKeywords.registerMetaKeyword(fromClass, entry.getKey(), entry.getValue());
         }
      } catch (Exception e) {
         throw new RuntimeException("Failed to register meta keyword: " + rawClass.getName(), e);
      }
   }

   public static Class<?> getRawClass(Type type) throws ClassNotFoundException {
      if (type instanceof ParameterizedType paramType) {
         return (Class<?>)paramType.getRawType();
      } else if (type instanceof Class<?> clazz) {
         return clazz;
      } else {
         throw new ClassNotFoundException("Unknown type argument: " + type);
      }
   }
}
