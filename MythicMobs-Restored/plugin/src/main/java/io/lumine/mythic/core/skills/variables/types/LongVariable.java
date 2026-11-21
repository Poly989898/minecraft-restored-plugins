package io.lumine.mythic.core.skills.variables.types;

import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.interfaces.NumericVariable;
import java.util.Objects;
import lombok.Generated;
import lombok.Getter;

@Getter
public class LongVariable extends Variable implements NumericVariable {
   private volatile Long value;
   public static final VariableInfo<Long> variableInfo = VariableInfo.<Long>builder()
      .clazz(LongVariable.class)
      .type(VariableType.LONG)
      .typeName("Long")
      .valueClass(Long.class)
      .defaultValue(PolymorphicPlaceholder.of("0"))
      .objectConstructor(LongVariable::ofObject)
      .polymorphicConstructor(LongVariable::of)
      .build();

   @Override
   public VariableInfo<Long> getVariableInfo() {
      return variableInfo;
   }

   public static LongVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new LongVariable(data.getLong(context), expirationTime);
   }

   public static String toString(Long data) {
      return Long.toString(data);
   }

   public static Long fromString(String data) {
      return Long.parseLong(data);
   }

   public static LongVariable ofObject(Object o, Long expirationTime) {
      return o instanceof Number numberNumber
         ? new LongVariable(numberNumber.longValue(), expirationTime)
         : new LongVariable(String.valueOf(o), expirationTime);
   }

   public LongVariable(long value) {
      this.value = value;
   }

   public LongVariable(long value, long expirationTime) {
      super(expirationTime);
      this.value = value;
   }

   public LongVariable(String value) {
      this(fromString(value));
   }

   public LongVariable(String value, long expirationTime) {
      this(fromString(value), expirationTime);
   }

   public Long get() {
      return this.value;
   }

   public Long getValueClone() {
      return this.value;
   }

   public synchronized void setValue(Long value) {
      this.value = value;
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      long amount = data.getLong(context);
      synchronized (this) {
         this.value = this.value + amount;
         return true;
      }
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      long amount = data.getLong(context);
      synchronized (this) {
         this.value = this.value - amount;
         return true;
      }
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      return Objects.equals(this.value, data.getLong(context));
   }

   @Override
   public String toString() {
      return this.value + "";
   }

   @Override
   public synchronized void executeMath(String eq) {
      this.value = (long)this.parseMath(eq, this.value.longValue());
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof LongVariable other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$value = this.getValue();
         Object other$value = other.getValue();
         return this$value == null ? other$value == null : this$value.equals(other$value);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof LongVariable;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $value = this.getValue();
      return result * 59 + ($value == null ? 43 : $value.hashCode());
   }
}
