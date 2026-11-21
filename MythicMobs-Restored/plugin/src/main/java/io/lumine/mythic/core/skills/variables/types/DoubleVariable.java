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
public class DoubleVariable extends Variable implements NumericVariable {
   private volatile double value;
   public static final VariableInfo<Double> variableInfo = VariableInfo.<Double>builder()
      .clazz(DoubleVariable.class)
      .type(VariableType.DOUBLE)
      .typeName("Double")
      .valueClass(Double.class)
      .defaultValue(PolymorphicPlaceholder.of("0"))
      .objectConstructor(DoubleVariable::ofObject)
      .polymorphicConstructor(DoubleVariable::of)
      .build();

   @Override
   public VariableInfo<Double> getVariableInfo() {
      return variableInfo;
   }

   public static DoubleVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new DoubleVariable(data.getDouble(context), expirationTime);
   }

   public static String toString(Double data) {
      return Double.toString(data);
   }

   public static Double fromString(String data) {
      return Double.parseDouble(data);
   }

   public static DoubleVariable ofObject(Object o, Long expirationTime) {
      return o instanceof Number numberNumber
         ? new DoubleVariable(numberNumber.doubleValue(), expirationTime)
         : new DoubleVariable(String.valueOf(o), expirationTime);
   }

   public DoubleVariable(double value) {
      this.value = value;
   }

   public DoubleVariable(double value, long expirationTime) {
      super(expirationTime);
      this.value = value;
   }

   public DoubleVariable(String value) {
      this(fromString(value));
   }

   public DoubleVariable(String value, long expirationTime) {
      this(fromString(value), expirationTime);
   }

   public Double get() {
      return this.value;
   }

   public Double getValueClone() {
      return this.value;
   }

   public synchronized void setValue(double value) {
      this.value = value;
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      double amount = data.getDouble(context);
      synchronized (this) {
         this.value += amount;
         return true;
      }
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      double amount = data.getDouble(context);
      synchronized (this) {
         this.value -= amount;
         return true;
      }
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      double newValue = data.getDouble(context);
      return this.value == newValue;
   }

   @Override
   public String toString() {
      return this.value + "";
   }

   @Override
   public synchronized void executeMath(String eq) {
      this.value = this.parseMath(eq, this.value);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DoubleVariable other)) {
         return false;
      } else {
         return !other.canEqual(this) ? false : Double.compare(this.getValue(), other.getValue()) == 0;
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof DoubleVariable;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $value = Double.doubleToLongBits(this.getValue());
      return result * 59 + (int)($value >>> 32 ^ $value);
   }
}
