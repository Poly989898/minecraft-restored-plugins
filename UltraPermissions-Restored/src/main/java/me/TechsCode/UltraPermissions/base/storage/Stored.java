package me.TechsCode.UltraPermissions.base.storage;

import java.util.Objects;
import java.util.Optional;
import me.TechsCode.UltraPermissions.base.misc.Getter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

public class Stored<T extends Storable> {
   private String key;
   private Getter<Storage<T>> storageGetter;
   private T cache;

   public static <T extends Storable> Stored<T> empty() {
      return new Stored<>(JsonNull.INSTANCE, null);
   }

   public Stored(String key, Getter<Storage<T>> storageGetter) {
      this.key = key;
      this.storageGetter = storageGetter;
   }

   public Stored(JsonElement jsonElement, Getter<Storage<T>> storageGetter) {
      this.key = jsonElement != null && !jsonElement.isJsonNull() ? jsonElement.getAsString() : null;
      this.storageGetter = storageGetter;
   }

   public Optional<T> get() {
      if (this.cache != null) {
         if (this.cache.isLinkedToStorage()) {
            return Optional.of(this.cache);
         }

         this.cache = null;
      }

      if (this.key == null) {
         return Optional.empty();
      }

      if (this.storageGetter == null) {
         return Optional.empty();
      }

      Storage<T> storage = this.storageGetter.get();
      if (storage == null) {
         return Optional.empty();
      }

      Optional<T> optional = storage.get().stream().filter(stored -> stored.getKey().equals(this.key)).findFirst();
      optional.ifPresent(stored -> this.cache = (T)stored);
      return optional;
   }

   public boolean isPresent() {
      return this.get().isPresent();
   }

   public String getKey() {
      return this.key;
   }

   public T orElse(T fallback) {
      Optional optional = this.get();
      return (T)optional.orElse(fallback);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         Stored stored = (Stored)object;
         return Objects.equals(this.key, stored.key);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.key);
   }
}
