package io.lumine.mythic.core.skills.variables.types;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.interfaces.NumericVariable;
import lombok.Generated;
import lombok.Getter;

@Getter
public class IntegerVariable extends Variable implements NumericVariable {
   private volatile int value;
   public static final VariableInfo<Integer> variableInfo = VariableInfo.<Integer>builder()
      .clazz(IntegerVariable.class)
      .type(VariableType.INTEGER)
      .typeName("Integer")
      .valueClass(Integer.class)
      .defaultValue(PolymorphicPlaceholder.of("0"))
      .objectConstructor(IntegerVariable::ofObject)
      .polymorphicConstructor(IntegerVariable::of)
      .build();

   @Override
   public VariableInfo<Integer> getVariableInfo() {
      return variableInfo;
   }

   public static IntegerVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new IntegerVariable(data.getInt(context), expirationTime);
   }

   public static String toString(Integer data) {
      return Integer.toString(data);
   }

   public static Integer fromString(String data) {
      return Integer.parseInt(data);
   }

   public static IntegerVariable ofObject(Object o, Long expirationTime) {
      return o instanceof Number numberNumber
         ? new IntegerVariable(numberNumber.intValue(), expirationTime)
         : new IntegerVariable(String.valueOf(o), expirationTime);
   }

   public IntegerVariable(int value) {
      this.value = value;
   }

   public IntegerVariable(int value, long expirationTime) {
      super(expirationTime);
      this.value = value;
   }

   public IntegerVariable(String value) {
      this(fromString(value));
   }

   public IntegerVariable(String value, long expirationTime) {
      this(fromString(value), expirationTime);
   }

   public Integer get() {
      return this.value;
   }

   public Integer getValueClone() {
      return this.value;
   }

   public synchronized void setValue(int value) {
      this.value = value;
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      int amount = data.getInt(context);
      synchronized (this) {
         this.value += amount;
         return true;
      }
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      int amount = data.getInt(context);
      synchronized (this) {
         this.value -= amount;
         return true;
      }
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      return this.value == data.getInt(context);
   }

   @Override
   public String toString() {
      return this.value + "";
   }

   @Override
   public synchronized void executeMath(String eq) {
      this.value = (int)this.parseMath(eq, this.value);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof IntegerVariable other)) {
         return false;
      } else {
         return !other.canEqual(this) ? false : this.getValue() == other.getValue();
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof IntegerVariable;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      return result * 59 + this.getValue();
   }
}
