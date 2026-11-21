package io.lumine.mythic.bukkit.utils.metadata;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

final class SimpleMetadataKey<T> implements MetadataKey<T> {
   private final String id;
   private final TypeToken<T> type;

   SimpleMetadataKey(String id, TypeToken<T> type) {
      this.id = id.toLowerCase();
      this.type = type;
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public TypeToken<T> getType() {
      return this.type;
   }

   @Override
   public T cast(Object object) throws ClassCastException {
      Preconditions.checkNotNull(object, "object");
      return (T)this.type.getRawType().cast(object);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof SimpleMetadataKey && ((SimpleMetadataKey)obj).getId().equals(this.id);
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }
}
