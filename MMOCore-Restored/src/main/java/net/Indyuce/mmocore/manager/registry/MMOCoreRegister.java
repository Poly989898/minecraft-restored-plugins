package net.Indyuce.mmocore.manager.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmocore.manager.MMOCoreManager;
import org.apache.commons.lang.Validate;

public abstract class MMOCoreRegister<T extends RegisteredObject> implements MMOCoreManager {
   protected final Map<String, T> registered = new HashMap<>();

   public void register(T t) {
      Validate.notNull(t, this.getRegisteredObjectName() + " cannot be null");
      Validate.isTrue(
         !this.registered.containsKey(t.getId()), "There is already a " + this.getRegisteredObjectName() + " registered with ID '" + t.getId() + "'"
      );
      this.registered.put(t.getId(), t);
   }

   public T get(String id) {
      return Objects.requireNonNull(this.registered.get(id), "Could not find " + this.getRegisteredObjectName() + " with ID '" + id + "'");
   }

   public boolean has(String id) {
      return this.registered.containsKey(id);
   }

   public Collection<T> getAll() {
      return this.registered.values();
   }

   public abstract String getRegisteredObjectName();
}
