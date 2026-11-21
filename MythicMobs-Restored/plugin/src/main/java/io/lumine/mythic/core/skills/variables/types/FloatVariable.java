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
public class FloatVariable extends Variable implements NumericVariable {
   private volatile float value;
   public static final VariableInfo<Float> variableInfo = VariableInfo.<Float>builder()
      .clazz(FloatVariable.class)
      .type(VariableType.FLOAT)
      .typeName("Float")
      .valueClass(Float.class)
      .defaultValue(PolymorphicPlaceholder.of("0"))
      .objectConstructor(FloatVariable::ofObject)
      .polymorphicConstructor(FloatVariable::of)
      .build();

   @Override
   public VariableInfo<Float> getVariableInfo() {
      return variableInfo;
   }

   public static FloatVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new FloatVariable(data.getFloat(context), expirationTime);
   }

   public static String toString(Float data) {
      return Float.toString(data);
   }

   public static Float fromString(String data) {
      return Float.parseFloat(data);
   }

   public static FloatVariable ofObject(Object o, Long expirationTime) {
      return o instanceof Number numberNumber
         ? new FloatVariable(numberNumber.floatValue(), expirationTime)
         : new FloatVariable(String.valueOf(o), expirationTime);
   }

   public FloatVariable(float value) {
      this.value = value;
   }

   public FloatVariable(float value, long expirationTime) {
      super(expirationTime);
      this.value = value;
   }

   public FloatVariable(String value) {
      this(fromString(value));
   }

   public FloatVariable(String value, long expirationTime) {
      this(fromString(value), expirationTime);
   }

   public Float get() {
      return this.value;
   }

   public Float getValueClone() {
      return this.value;
   }

   public synchronized void setValue(float value) {
      this.value = value;
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      float amount = data.getFloat(context);
      synchronized (this) {
         this.value += amount;
         return true;
      }
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      float amount = data.getFloat(context);
      synchronized (this) {
         this.value -= amount;
         return true;
      }
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      float newValue = data.getFloat(context);
      return newValue == this.value;
   }

   @Override
   public String toString() {
      return this.value + "";
   }

   @Override
   public synchronized void executeMath(String eq) {
      this.value = (float)this.parseMath(eq, this.value);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof FloatVariable other)) {
         return false;
      } else {
         return !other.canEqual(this) ? false : Float.compare(this.getValue(), other.getValue()) == 0;
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof FloatVariable;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      return result * 59 + Float.floatToIntBits(this.getValue());
   }
}
