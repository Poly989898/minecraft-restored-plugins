package io.lumine.mythic.core.skills.placeholders;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import java.util.Optional;

public interface PlaceholderMeta {
   default SkillCaster getCaster() {
      return null;
   }

   default AbstractEntity getTrigger() {
      return null;
   }

   PlaceholderMeta setMetadata(String str, Object obj);

   default PlaceholderMeta setMetadata(PlaceholderMetadataKey key, Object value) {
      return this.setMetadata(key.getKey(), value);
   }

   Optional<Object> getMetadata(String str);

   default Optional<Object> getMetadata(PlaceholderMetadataKey key) {
      return this.getMetadata(key.getKey());
   }

   <T> Optional<T> getMetadata(Class<T> clazz, String str);

   Optional<Object> removeMetadata(String str);

   <T> Optional<T> removeMetadata(Class<T> clazz, String str);

   void clearMetadata();
}
