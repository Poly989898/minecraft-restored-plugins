package io.lumine.mythic.api.adapters;

import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.drops.EquipSlot;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

public record AbstractAttributeModifier(NamespacedKey key, UUID uniqueId, PlaceholderDouble value, AbstractAttributeModifier.Operation operation, EquipSlot slot) {
   @Getter
   public static enum Operation {
      ADD(0),
      ADD_NUMBER(0),
      MULTIPLY_BASE(1),
      ADD_SCALAR(1),
      MULTIPLY(2),
      MULTIPLY_SCALAR(2);

      private final int operation;
      private static final HashMap<String, AbstractAttributeModifier.Operation> BY_ALIAS = new HashMap<>();

      private Operation(int operation) {
         this.operation = operation;
      }

      @Nullable
      public static AbstractAttributeModifier.Operation parse(String value) {
         return BY_ALIAS.get(value);
      }

      static {
         for (AbstractAttributeModifier.Operation value : values()) {
            BY_ALIAS.put(value.name(), value);
            BY_ALIAS.put(String.valueOf(value.operation), value);
         }
      }
   }
}
