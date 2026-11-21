package io.lumine.mythic.bukkit.utils.metadata;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Supplier;

public interface MetadataMap {
   static MetadataMap create() {
      return new SimpleMetadataMap();
   }

   <T> void put(MetadataKey<T> metadataKey, T t);

   <T> void put(MetadataKey<T> metadataKey, TransientValue<T> transientValue);

   <T> void forcePut(MetadataKey<T> metadataKey, T t);

   <T> void forcePut(MetadataKey<T> metadataKey, TransientValue<T> transientValue);

   <T> boolean putIfAbsent(MetadataKey<T> metadataKey, T t);

   <T> boolean putIfAbsent(MetadataKey<T> metadataKey, TransientValue<T> transientValue);

   <T> Optional<T> get(MetadataKey<T> metadataKey);

   <T> T getOrNull(MetadataKey<T> metadataKey);

   <T> T getOrDefault(MetadataKey<T> metadataKey, T t);

   <T> T getOrPut(MetadataKey<T> metadataKey, Supplier<T> supplier);

   <T> T getOrPutExpiring(MetadataKey<T> metadataKey, Supplier<TransientValue<T>> supplier);

   boolean has(MetadataKey<?> metadataKey);

   boolean remove(MetadataKey<?> metadataKey);

   void clear();

   ImmutableMap<MetadataKey<?>, Object> asMap();

   boolean isEmpty();
}
