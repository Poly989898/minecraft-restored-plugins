package io.lumine.mythic.core.skills.variables;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import java.util.function.BiFunction;

public record VariableInfo<T>(
   Class<? extends Variable> clazz,
   VariableType type,
   String typeName,
   Class<T> valueClass,
   PolymorphicPlaceholder defaultValue,
   BiFunction<Object, Long, ? extends Variable> objectConstructor,
   VariableUtils.VariablePolymorphicFactory<? extends Variable> polymorphicConstructor
) {
   public static <T> VariableInfo.Builder<T> builder() {
      return new VariableInfo.Builder<>();
   }

   public static class Builder<T> {
      private Class<? extends Variable> clazz;
      private VariableType type;
      private String typeName;
      private Class<T> valueClass;
      PolymorphicPlaceholder defaultValue;
      private BiFunction<Object, Long, ? extends Variable> objectConstructor;
      private VariableUtils.VariablePolymorphicFactory<? extends Variable> polymorphicConstructor;

      public VariableInfo.Builder<T> clazz(Class<? extends Variable> clazz) {
         this.clazz = clazz;
         return this;
      }

      public VariableInfo.Builder<T> type(VariableType type) {
         this.type = type;
         return this;
      }

      public VariableInfo.Builder<T> typeName(String typeName) {
         this.typeName = typeName;
         return this;
      }

      public VariableInfo.Builder<T> valueClass(Class<T> valueClass) {
         this.valueClass = valueClass;
         return this;
      }

      public VariableInfo.Builder<T> defaultValue(PolymorphicPlaceholder defaultValue) {
         this.defaultValue = defaultValue;
         return this;
      }

      public VariableInfo.Builder<T> objectConstructor(BiFunction<Object, Long, ? extends Variable> objectConstructor) {
         this.objectConstructor = objectConstructor;
         return this;
      }

      public VariableInfo.Builder<T> polymorphicConstructor(VariableUtils.VariablePolymorphicFactory<? extends Variable> polymorphicConstructor) {
         this.polymorphicConstructor = polymorphicConstructor;
         return this;
      }

      public VariableInfo<T> build() {
         if (this.clazz == null) {
            throw new IllegalArgumentException("Variable class cannot be null");
         } else if (this.type == null) {
            throw new IllegalArgumentException("Variable type cannot be null");
         } else if (this.typeName != null && !this.typeName.isEmpty()) {
            return new VariableInfo<>(
               this.clazz, this.type, this.typeName, this.valueClass, this.defaultValue, this.objectConstructor, this.polymorphicConstructor
            );
         } else {
            throw new IllegalArgumentException("Variable type name cannot be null or empty");
         }
      }
   }
}
