package io.lumine.mythic.core.skills.placeholders.metakeywords;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.types.MetaKeywordsSegment;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializationRegistry;
import io.lumine.mythic.core.skills.variables.Variable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Generated;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

public final class MetaKeywords {
   static final Map<Class<?>, Map<String, MetaKeywords.MetaKeywordsDefinition<?, ?>>> metaKeywordsRegistry = new ConcurrentHashMap<>();
   static final Map<String, String> metaKeywordsCache = new ConcurrentHashMap<>();

   public static <T> void registerMetaKeywords(Class<T> clazz, Map<String, MetaKeywords.MetaKeywordsDefinition<T, ?>> extractors) {
      Map<String, MetaKeywords.MetaKeywordsDefinition<?, ?>> castedMap = new ConcurrentHashMap<>(extractors);
      if (metaKeywordsRegistry.containsKey(clazz)) {
         metaKeywordsRegistry.get(clazz).putAll(castedMap);
      } else {
         metaKeywordsRegistry.put(clazz, castedMap);
      }
   }

   public static <T, K> void registerMetaKeyword(Class<T> startClass, String key, MetaKeywords.MetaKeywordsDefinition<T, K> definition) {
      metaKeywordsRegistry.computeIfAbsent(startClass, k -> new ConcurrentHashMap<>()).put(key.toLowerCase(), definition);
   }

   public static <T, K> void registerMetaKeyword(Class<T> startClass, String key, MetaKeywords.MetaKeywordsExtractor<T, K> extractor, Class<K> returnType) {
      Entry<String, MetaKeywords.MetaKeywordsDefinition<T, K>> entry = add(key, extractor, returnType);
      if (!metaKeywordsRegistry.containsKey(startClass)) {
         metaKeywordsRegistry.put(startClass, new ConcurrentHashMap<>());
      }

      metaKeywordsRegistry.get(startClass).put(entry.getKey(), entry.getValue());
   }

   public static <T, K> Entry<String, MetaKeywords.MetaKeywordsDefinition<T, K>> add(
      String key, MetaKeywords.MetaKeywordsExtractor<T, K> extractor, Class<K> returnType
   ) {
      return add(key, 0, new HashMap<>(), extractor, returnType);
   }

   public static <T, K> Entry<String, MetaKeywords.MetaKeywordsDefinition<T, K>> add(
      String key,
      int additionalKeys,
      Collection<MetaKeywords.MetaKeywordsAttribute> attributes,
      MetaKeywords.MetaKeywordsExtractor<T, K> extractor,
      Class<K> returnType
   ) {
      return add(key, additionalKeys, MetaKeywords.MetaKeywordsDefinition.registerAttributes(attributes), extractor, returnType);
   }

   public static <T, K> Entry<String, MetaKeywords.MetaKeywordsDefinition<T, K>> add(
      String key,
      int additionalKeys,
      Map<String, MetaKeywords.MetaKeywordsAttribute> attributes,
      MetaKeywords.MetaKeywordsExtractor<T, K> extractor,
      Class<K> returnType
   ) {
      return Map.entry(key.toLowerCase(), new MetaKeywords.MetaKeywordsDefinition<>(extractor, additionalKeys + 1, attributes, returnType));
   }

   static String getCacheKey(String value, List<PolymorphicPlaceholder> metaKeywords) {
      StringBuilder sb = new StringBuilder(value);
      if (metaKeywords != null && !metaKeywords.isEmpty()) {
         sb.append("@");

         for (int i = 0; i < metaKeywords.size(); i++) {
            sb.append(metaKeywords.get(i).originalValue());
            if (i < metaKeywords.size() - 1) {
               sb.append(".");
            }
         }
      }

      return sb.toString();
   }

   static <T> String processCacheOperation(T value, Class<T> type, MetaKeywords.ExtractionContext context) {
      String cacheKey = getCacheKey(type.getName() + ":" + value.toString(), context.getRemainingKeys());
      return metaKeywordsCache.computeIfAbsent(cacheKey, key -> String.valueOf(processMetaKeywords(value, type, context)));
   }

   public static void registerConversionMetaKeywords() {
      PlaceholderClassSerializationRegistry.SerializationMap
         .forEach(
            (startClass, serializer) -> {
               Function<Object, String> formatter = (Function<Object, String>)PlaceholderClassSerializationRegistry.FormattingMap.get(startClass);
               registerMetaKeyword((Class<?>)startClass, "formatted", handler -> formatter.apply(handler.value), String.class);
               String typeName = PlaceholderClassSerializationRegistry.NameMap.get(startClass);
               registerMetaKeyword((Class<?>)startClass, "typeName", handler -> typeName, String.class);
               PlaceholderClassSerializationRegistry.DeserializationMap
                  .forEach(
                     (finalClass, deserializer) -> {
                        String keyword = "to" + PlaceholderClassSerializationRegistry.NameMap.get(finalClass);
                        MetaKeywords.MetaKeywordsExtractor<Object, Object> extractor = handler -> deserializer.apply(
                           ((Function<Object, String>)serializer).apply(handler.value)
                        );
                        registerMetaKeyword((Class<Object>)(Class)startClass, keyword, extractor, (Class<Object>)finalClass);
                     }
                  );
            }
         );
   }

   public static void registerAll() {
      registerConversionMetaKeywords();
      MetaKeywordRegistrar.registerAll("io.lumine.mythic.core.skills.placeholders.metakeywords");
   }

   public static <T> String resolveMetaKeywords(MetaKeywords.ExtractionContext context, T value, Class<T> type) {
      Object ret = processMetaKeywords(value, type, context);
      return String.valueOf(ret);
   }

   public static String resolveMetaKeywords(MetaKeywords.ExtractionContext context, Variable variable) {
      Object ret = processMetaKeywords(variable.getValueClone(), variable.getValueClass(), context);
      return String.valueOf(ret);
   }

   public static <T> Object processMetaKeywords(T value, Class<T> type, MetaKeywords.ExtractionContext context) {
      String keyToMatch = context.getString(0).toLowerCase();
      if (keyToMatch.equals("cache")) {
         return processCacheOperation(value, type, context.shift(1));
      } else {
         Map<String, MetaKeywords.MetaKeywordsDefinition<?, ?>> extractorMap = metaKeywordsRegistry.getOrDefault(type, metaKeywordsRegistry.get(Object.class));
         MetaKeywords.MetaKeywordsDefinition<T, ?> specificDefinition = (MetaKeywords.MetaKeywordsDefinition<T, ?>)extractorMap.get(keyToMatch);
         if (specificDefinition != null) {
            return executeMetadataExtraction(specificDefinition, value, context);
         } else {
            MetaKeywords.MetaKeywordsDefinition<T, ?> defaultDefinitionForSpecificType = (MetaKeywords.MetaKeywordsDefinition<T, ?>)extractorMap.get("default");
            return defaultDefinitionForSpecificType != null
               ? executeMetadataExtraction(defaultDefinitionForSpecificType, value, context)
               : "NO_MATCHING_METADATA";
         }
      }
   }

   static <T, K> Object executeMetadataExtraction(MetaKeywords.MetaKeywordsDefinition<T, K> definition, T value, MetaKeywords.ExtractionContext context) {
      MetaKeywords.ExtractionHandler<T, K> handler = new MetaKeywords.ExtractionHandler<>(value, context, definition);
      int requiredSegments = handler.getCurrentSegment().config() != null ? 1 : definition.requiredSegments();
      if (requiredSegments > context.length()) {
         return "NOT_ENOUGH_KEYS_IN_VARIABLE_METADATA";
      } else {
         K extracted = definition.extractor().extract(handler);
         if (extracted == null) {
            return "UNDEFINED";
         } else if (requiredSegments < context.length()) {
            return processMetaKeywords(extracted, definition.returnType, context.shift(requiredSegments));
         } else {
            Function<Object, String> finalConversion = (Function<Object, String>)PlaceholderClassSerializationRegistry.SerializationMap
               .get(definition.returnType);
            return finalConversion.apply(extracted);
         }
      }
   }

   public static class ExtractionContext {
      final PlaceholderContext placeholderContext;
      final List<MetaKeywordsSegment> segments;
      int index;

      public ExtractionContext(PlaceholderContext placeholderContext, List<MetaKeywordsSegment> segments) {
         this.placeholderContext = placeholderContext;
         this.segments = segments;
         this.index = 0;
      }

      public PolymorphicPlaceholder get(int i) {
         return this.segments.get(this.index + i).key();
      }

      public PolymorphicPlaceholder get() {
         return this.segments.get(this.index).key();
      }

      public int length() {
         return this.segments.size() - this.index;
      }

      public List<PolymorphicPlaceholder> getRemainingKeys() {
         return this.segments.subList(this.index, this.segments.size()).stream().map(MetaKeywordsSegment::key).collect(Collectors.toList());
      }

      public MetaKeywords.ExtractionContext shift(int i) {
         this.index += i;
         return this;
      }

      <T> T getObject(int indexOffset, BiFunction<PolymorphicPlaceholder, PlaceholderContext, T> function) {
         return function.apply(this.get(indexOffset), this.placeholderContext);
      }

      public int getInt(int indexOffset) {
         return this.get(indexOffset).getInt(this.placeholderContext);
      }

      public String getString(int indexOffset) {
         return this.get(indexOffset).getString(this.placeholderContext);
      }

      MetaKeywordsSegment getCurrentSegment() {
         return this.segments.get(this.index);
      }
   }

   public record ExtractionHandler<T, K>(T value, MetaKeywords.ExtractionContext context, MetaKeywords.MetaKeywordsDefinition<T, K> definition) {
      PolymorphicPlaceholder getAttribute(String identifier) {
         MetaKeywords.MetaKeywordsAttribute attribute = this.definition.attributes.get(identifier);
         if (attribute == null) {
            throw new IllegalStateException("MetaKeyword attribute '" + identifier + "' not registered for key '" + this.context.getString(0) + "'");
         } else {
            MetaKeywordsSegment segment = this.getCurrentSegment();
            PolymorphicPlaceholder polymorphicPlaceholder = segment.attributes().get(identifier);
            if (polymorphicPlaceholder != null) {
               return polymorphicPlaceholder;
            } else if (segment.config() == null) {
               if (attribute.index > 0) {
                  return this.context.get(attribute.index);
               } else {
                  segment.attributes().put(identifier, PolymorphicPlaceholder.of(attribute.defaultValue));
                  return segment.attributes().get(identifier);
               }
            } else {
               PolymorphicPlaceholder newPolymorphicPlaceholder = segment.config().getPolymorphicPlaceholder(attribute.names, attribute.defaultValue);
               segment.attributes().put(identifier, newPolymorphicPlaceholder);
               return newPolymorphicPlaceholder;
            }
         }
      }

      MetaKeywordsSegment getCurrentSegment() {
         return this.context.getCurrentSegment();
      }

      public String getString(MetaKeywords.MetaKeywordsAttribute attribute) {
         return this.getString(attribute.identifier);
      }

      public String getString(String identifier) {
         return this.getAttribute(identifier).getString(this.context.placeholderContext);
      }

      public int getInt(MetaKeywords.MetaKeywordsAttribute attribute) {
         return this.getInt(attribute.identifier);
      }

      public int getInt(String identifier) {
         return this.getAttribute(identifier).getInt(this.context.placeholderContext);
      }

      public <L> L getObject(MetaKeywords.MetaKeywordsAttribute attribute, BiFunction<PolymorphicPlaceholder, PlaceholderContext, L> function) {
         return this.getObject(attribute.identifier, function);
      }

      public <L> L getObject(String identifier, BiFunction<PolymorphicPlaceholder, PlaceholderContext, L> function) {
         return function.apply(this.getAttribute(identifier), this.context.placeholderContext);
      }
   }

   public static class MetaKeywordsAttribute {
      int index;
      @NotNull
      String identifier;
      @NotNull
      String[] names;
      @NotNull
      String defaultValue;

      @Generated
      protected MetaKeywordsAttribute(MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilder<?, ?> b) {
         this.index = b.index;
         this.identifier = b.identifier;
         if (this.identifier == null) {
            throw new NullPointerException("identifier is marked non-null but is null");
         } else {
            this.names = b.names;
            if (this.names == null) {
               throw new NullPointerException("names is marked non-null but is null");
            } else {
               this.defaultValue = b.defaultValue;
               if (this.defaultValue == null) {
                  throw new NullPointerException("defaultValue is marked non-null but is null");
               }
            }
         }
      }

      @Generated
      public static MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilder<?, ?> builder() {
         return new MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilderImpl();
      }

      @Generated
      public MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilder<?, ?> toBuilder() {
         return new MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilderImpl().$fillValuesFrom(this);
      }

      @ToString
      @Generated
      public abstract static class MetaKeywordsAttributeBuilder<C extends MetaKeywords.MetaKeywordsAttribute, B extends MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilder<C, B>> {
         @Generated
         private int index;
         @Generated
         private String identifier;
         @Generated
         private String[] names;
         @Generated
         private String defaultValue;

         @Generated
         protected B $fillValuesFrom(C instance) {
            $fillValuesFromInstanceIntoBuilder(instance, this);
            return this.self();
         }

         @Generated
         private static void $fillValuesFromInstanceIntoBuilder(
            MetaKeywords.MetaKeywordsAttribute instance, MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilder<?, ?> b
         ) {
            b.index(instance.index);
            b.identifier(instance.identifier);
            b.names(instance.names);
            b.defaultValue(instance.defaultValue);
         }

         @Generated
         public B index(int index) {
            this.index = index;
            return this.self();
         }

         @Generated
         public B identifier(@NotNull String identifier) {
            if (identifier == null) {
               throw new NullPointerException("identifier is marked non-null but is null");
            } else {
               this.identifier = identifier;
               return this.self();
            }
         }

         @Generated
         public B names(@NotNull String[] names) {
            if (names == null) {
               throw new NullPointerException("names is marked non-null but is null");
            } else {
               this.names = names;
               return this.self();
            }
         }

         @Generated
         public B defaultValue(@NotNull String defaultValue) {
            if (defaultValue == null) {
               throw new NullPointerException("defaultValue is marked non-null but is null");
            } else {
               this.defaultValue = defaultValue;
               return this.self();
            }
         }

         @Generated
         protected abstract B self();

         @Generated
         public abstract C build();
      }

      @Generated
      private static final class MetaKeywordsAttributeBuilderImpl
         extends MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilder<MetaKeywords.MetaKeywordsAttribute, MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilderImpl> {
         @Generated
         protected MetaKeywords.MetaKeywordsAttribute.MetaKeywordsAttributeBuilderImpl self() {
            return this;
         }

         @Generated
         @Override
         public MetaKeywords.MetaKeywordsAttribute build() {
            return new MetaKeywords.MetaKeywordsAttribute(this);
         }
      }
   }

   public record MetaKeywordsDefinition<T, K>(
      MetaKeywords.MetaKeywordsExtractor<T, K> extractor, int requiredSegments, Map<String, MetaKeywords.MetaKeywordsAttribute> attributes, Class<K> returnType
   ) {
      static Map<String, MetaKeywords.MetaKeywordsAttribute> registerAttributes(Collection<MetaKeywords.MetaKeywordsAttribute> collection) {
         Map<String, MetaKeywords.MetaKeywordsAttribute> result = new HashMap<>();
         collection.forEach(element -> result.put(element.identifier, element));
         return Collections.unmodifiableMap(result);
      }
   }

   @FunctionalInterface
   public interface MetaKeywordsExtractor<T, K> {
      K extract(MetaKeywords.ExtractionHandler<T, K> extractionHandler);
   }
}
