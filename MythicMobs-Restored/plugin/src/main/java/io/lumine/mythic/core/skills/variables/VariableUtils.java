package io.lumine.mythic.core.skills.variables;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import java.util.Map;
import java.util.Map.Entry;

public class VariableUtils {
   public static <T, K> boolean registerOnMapUtil(Map<T, K> map, T key, K value) {
      if (map.containsKey(key)) {
         return false;
      } else {
         map.put(key, value);
         return true;
      }
   }

   public static Variable createVariableFromPrefixedValue(String value) {
      for (Entry<String, VariableInfo<?>> entry : VariableInfoRegistry.defaultVariablesPrefixes.entrySet()) {
         if (value.toLowerCase().startsWith(entry.getKey())) {
            return entry.getValue().objectConstructor().apply(value.substring(entry.getKey().length()), 0L);
         }
      }

      if (value.startsWith("int/")) {
         int val = Integer.parseInt(value.substring(4));
         return new IntegerVariable(val, 0L);
      } else {
         return new StringVariable(value, 0L);
      }
   }

   public static VariableType getType(Class<? extends Variable> clazz) {
      VariableInfo<?> info = VariableInfoRegistry.get(clazz);
      if (info != null && info.type() != null) {
         return info.type();
      } else {
         throw new IllegalArgumentException("Invalid variable class (" + clazz.getName() + ").");
      }
   }

   public static String getTypeName(Class<? extends Variable> clazz) {
      VariableInfo<?> info = VariableInfoRegistry.get(clazz);
      if (info == null) {
         throw new IllegalArgumentException("Invalid variable class (" + clazz.getName() + ").");
      } else {
         return info.typeName();
      }
   }

   public static Class<? extends Variable> getTypeClass(String type) {
      VariableInfo<?> info = VariableInfoRegistry.get(type);
      if (info != null && info.clazz() != null) {
         return info.clazz();
      } else {
         throw new IllegalArgumentException("Invalid variable type : " + type + ".");
      }
   }

   @FunctionalInterface
   public interface VariableFromStringFactory<T> {
      T convert(String str);
   }

   @FunctionalInterface
   public interface VariablePolymorphicFactory<T extends Variable> {
      T create(PolymorphicPlaceholder polymorphicPlaceholder, PlaceholderContext placeholderContext, long value);
   }

   @FunctionalInterface
   public interface VariableToStringFactory<T> {
      String convert(T t);
   }
}
