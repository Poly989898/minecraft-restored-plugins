package io.lumine.mythic.core.skills.variables;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Variable implements Cloneable {
   private long expirationTime = 0L;

   public Variable() {
   }

   public Variable(long expirationTime) {
      this.expirationTime = expirationTime;
   }

   public boolean shouldExpire() {
      return this.expirationTime > 1L && this.expirationTime <= System.currentTimeMillis();
   }

   public boolean shouldSave() {
      return !this.shouldExpire() && this.expirationTime != 1L;
   }

   public abstract <T> VariableInfo<T> getVariableInfo();

   public abstract Object get();

   public abstract Object getValueClone();

   public VariableType getType() {
      return this.getVariableInfo().type();
   }

   public String getTypeName() {
      return this.getVariableInfo().typeName();
   }

   public <T> Class<T> getValueClass() {
      return this.<T>getVariableInfo().valueClass();
   }

   public abstract boolean executeAddition(PolymorphicPlaceholder polymorphicPlaceholder, PlaceholderContext placeholderContext);

   public abstract boolean executeSubtraction(PolymorphicPlaceholder polymorphicPlaceholder, PlaceholderContext placeholderContext);

   public abstract boolean checkEquality(PolymorphicPlaceholder polymorphicPlaceholder, PlaceholderContext placeholderContext);

   @Override
   public abstract String toString();

   public String formatted() {
      return this.toString();
   }

   public void unregister() {
   }

   public Variable clone() {
      try {
         return (Variable)super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

   public static Variable toType(Variable var, VariableType type) {
      return var == null ? null : ofType(type, var);
   }

   public static Variable ofType(VariableType type, Object value) {
      return ofType(type, value, 1L);
   }

   public static Variable ofType(VariableType type, Object value, boolean save) {
      return ofType(type, value, save ? 0L : 1L);
   }

   public static Variable ofType(VariableType type, Object value, long expire) {
      VariableInfo<?> info = VariableInfoRegistry.get(type);
      if (info == null) {
         return null;
      } else if (info.objectConstructor() == null) {
         return null;
      } else {
         if (value instanceof Variable var) {
            value = var.toString();
         }

         return info.objectConstructor().apply(value, expire);
      }
   }
}
