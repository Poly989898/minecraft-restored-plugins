package com.ticxo.modelengine.api.model.bone.behavior;

import java.util.Map;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class BoneBehaviorData {
   private final Map<String, Object> data;

   @Nullable
   public <T> T get(String key) {
      try {
         return (T)this.data.get(key);
      } catch (ClassCastException e) {
         e.printStackTrace();
         return null;
      }
   }

   @NotNull
   public <T> T get(String key, @NotNull T def) {
      Object value = this.data.get(key);
      if (value == null) {
         return def;
      }

      Class<?> defClass = def.getClass();
      Class<?> valClass = value.getClass();
      if (defClass.isAssignableFrom(valClass)) {
         return (T)value;
      }

      new ClassCastException(String.format("Could not cast %s to %s. Returning default value.", valClass.getSimpleName(), defClass.getSimpleName()))
         .printStackTrace();
      return def;
   }
}
