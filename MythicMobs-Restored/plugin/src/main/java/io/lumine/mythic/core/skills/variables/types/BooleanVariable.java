package io.lumine.mythic.core.skills.variables.types;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BooleanVariable extends Variable {
   private boolean value;
   public static final VariableInfo<Boolean> variableInfo = VariableInfo.<Boolean>builder()
      .clazz(BooleanVariable.class)
      .type(VariableType.BOOLEAN)
      .typeName("Boolean")
      .valueClass(Boolean.class)
      .defaultValue(PolymorphicPlaceholder.of("false"))
      .objectConstructor(BooleanVariable::ofObject)
      .polymorphicConstructor(BooleanVariable::of)
      .build();

   @Override
   public VariableInfo<Boolean> getVariableInfo() {
      return variableInfo;
   }

   public static BooleanVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new BooleanVariable(data.getBoolean(context), expirationTime);
   }

   public static String toString(Boolean data) {
      return data.toString();
   }

   public static Boolean fromString(String data) {
      return PolymorphicPlaceholder.of(data).getBoolean(PlaceholderContext.of());
   }

   public static BooleanVariable ofObject(Object value, long expirationTime) {
      if (value == null) {
         return new BooleanVariable(false, expirationTime);
      } else {
         return value instanceof Boolean bool ? new BooleanVariable(bool, expirationTime) : new BooleanVariable(value.toString(), expirationTime);
      }
   }

   public BooleanVariable(boolean value) {
      this.value = value;
   }

   public BooleanVariable(boolean value, long expirationTime) {
      super(expirationTime);
      this.value = value;
   }

   public BooleanVariable(String value) {
      this.build(value);
   }

   public BooleanVariable(String value, long expirationTime) {
      super(expirationTime);
      this.build(value);
   }

   public void build(String value) {
      this.value = fromString(value);
   }

   public Boolean get() {
      return this.value;
   }

   public Boolean getValueClone() {
      return this.value;
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      boolean newValue = data.getBoolean(context);
      this.value = this.value || newValue;
      return true;
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      boolean newValue = data.getBoolean(context);
      this.value = this.value && newValue;
      return true;
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      boolean newValue = data.getBoolean(context);
      return this.value == newValue;
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BooleanVariable other)) {
         return false;
      } else {
         return !other.canEqual(this) ? false : this.isValue() == other.isValue();
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof BooleanVariable;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      return result * 59 + (this.isValue() ? 79 : 97);
   }
}
