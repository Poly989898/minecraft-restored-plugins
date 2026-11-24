package io.lumine.mythic.lib.api.stat.api;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.util.Closeable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ModifiedInstance<T extends InstanceModifier> {
   protected final Map<UUID, T> modifiers = new ConcurrentHashMap<>();

   public double getTotal(double base) {
      return this.getFilteredTotal(base, EquipmentSlot.MAIN_HAND::isCompatible, mod -> mod);
   }

   public double getFilteredTotal(double base, Predicate<T> filter) {
      return this.getFilteredTotal(base, filter, mod -> mod);
   }

   public double getTotal(double base, Function<T, T> modification) {
      return this.getFilteredTotal(base, EquipmentSlot.MAIN_HAND::isCompatible, modification);
   }

   public double getFilteredTotal(double base, Predicate<T> filter, Function<T, T> modification) {
      for (T mod : this.modifiers.values()) {
         if (mod.getType() == ModifierType.FLAT && filter.test(mod)) {
            base += modification.apply(mod).getValue();
         }
      }

      for (T mod : this.modifiers.values()) {
         if (mod.getType() == ModifierType.RELATIVE && filter.test(mod)) {
            base *= 1.0 + modification.apply(mod).getValue() / 100.0;
         }
      }

      return base;
   }

   @Deprecated
   @Nullable
   public T getModifier(@Nullable String key) {
      for (T modifier : this.modifiers.values()) {
         if (modifier.getKey().equals(key)) {
            return modifier;
         }
      }

      return null;
   }

   @Nullable
   public T getModifier(@NotNull UUID uniqueId) {
      return this.modifiers.get(uniqueId);
   }

   @Deprecated
   public void addModifier(@NotNull T modifier) {
      this.removeIf(modifier.getKey()::equals);
      this.registerModifier(modifier);
   }

   public void registerModifier(@NotNull T modifier) {
      this.modifiers.put(modifier.getUniqueId(), modifier);
   }

   public void removeModifier(@NotNull UUID uniqueId) {
      this.modifiers.remove(uniqueId);
   }

   @Deprecated
   public void remove(@NotNull String key) {
      this.removeIf(key::equals);
   }

   public boolean isEmpty() {
      return this.modifiers.isEmpty();
   }

   public void removeIf(@NotNull Predicate<String> condition) {
      Iterator<T> iterator = this.modifiers.values().iterator();

      while (iterator.hasNext()) {
         T modifier = iterator.next();
         if (condition.test(modifier.getKey())) {
            if (modifier instanceof Closeable) {
               ((Closeable)modifier).close();
            }

            iterator.remove();
         }
      }
   }

   @NotNull
   public Collection<T> getModifiers() {
      return this.modifiers.values();
   }

   @NotNull
   public Set<UUID> getIds() {
      return this.modifiers.keySet();
   }

   @Deprecated
   @NotNull
   public Set<String> getKeys() {
      return this.modifiers.values().stream().map(mod -> mod.getKey()).collect(Collectors.toSet());
   }

   public boolean contains(String key) {
      return this.modifiers.containsKey(key);
   }
}
