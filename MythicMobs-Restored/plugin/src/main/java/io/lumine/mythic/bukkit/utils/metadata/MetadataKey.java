package io.lumine.mythic.bukkit.utils.metadata;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.cooldown.Cooldown;
import java.util.UUID;

public interface MetadataKey<T> {
   static <T> MetadataKey<T> create(String id, TypeToken<T> type) {
      Preconditions.checkNotNull(id, "id");
      Preconditions.checkNotNull(type, "type");
      return new SimpleMetadataKey<>(id, type);
   }

   static <T> MetadataKey<T> create(String id, Class<T> clazz) {
      return create(id, TypeToken.of(clazz));
   }

   static MetadataKey<Empty> createEmptyKey(String id) {
      return create(id, Empty.class);
   }

   static MetadataKey<String> createStringKey(String id) {
      return create(id, String.class);
   }

   static MetadataKey<Boolean> createBooleanKey(String id) {
      return create(id, Boolean.class);
   }

   static MetadataKey<Integer> createIntegerKey(String id) {
      return create(id, Integer.class);
   }

   static MetadataKey<Long> createLongKey(String id) {
      return create(id, Long.class);
   }

   static MetadataKey<Double> createDoubleKey(String id) {
      return create(id, Double.class);
   }

   static MetadataKey<Float> createFloatKey(String id) {
      return create(id, Float.class);
   }

   static MetadataKey<Short> createShortKey(String id) {
      return create(id, Short.class);
   }

   static MetadataKey<Character> createCharacterKey(String id) {
      return create(id, Character.class);
   }

   static MetadataKey<Cooldown> createCooldownKey(String id) {
      return create(id, Cooldown.class);
   }

   static MetadataKey<UUID> createUuidKey(String id) {
      return create(id, UUID.class);
   }

   String getId();

   TypeToken<T> getType();

   T cast(Object obj) throws ClassCastException;
}
