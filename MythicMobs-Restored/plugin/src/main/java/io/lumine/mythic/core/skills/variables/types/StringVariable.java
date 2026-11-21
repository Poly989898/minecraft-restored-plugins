package io.lumine.mythic.core.skills.variables.types;

import com.google.common.base.Preconditions;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.interfaces.ContainingVariable;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringVariable extends Variable implements ContainingVariable {
   private String value;
   public static final VariableInfo<String> variableInfo = VariableInfo.<String>builder()
      .clazz(StringVariable.class)
      .type(VariableType.STRING)
      .typeName("String")
      .valueClass(String.class)
      .defaultValue(PolymorphicPlaceholder.of(""))
      .objectConstructor(StringVariable::ofObject)
      .polymorphicConstructor(StringVariable::of)
      .build();

   @Override
   public VariableInfo<String> getVariableInfo() {
      return variableInfo;
   }

   public static StringVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new StringVariable(data.getString(context), expirationTime);
   }

   public static String toString(String data) {
      return data;
   }

   public static String fromString(String data) {
      return data;
   }

   public static StringVariable ofObject(Object o, Long expirationTime) {
      return o instanceof String string ? new StringVariable(string, expirationTime) : new StringVariable(String.valueOf(o), expirationTime);
   }

   public StringVariable(String value) {
      Preconditions.checkNotNull(value);
      this.value = value;
   }

   public StringVariable(String value, long expirationTime) {
      super(expirationTime);
      Preconditions.checkNotNull(value);
      this.value = value;
   }

   public String substring(int start, int end) {
      if (start < 0) {
         start = 0;
      }

      if (end > this.value.length()) {
         end = this.value.length();
      }

      if (start >= this.value.length()) {
         return "";
      } else {
         return start > end ? "" : this.value.substring(start, end);
      }
   }

   public String get() {
      return this.value;
   }

   public String getValueClone() {
      return this.value;
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      this.value = this.value + data.getString(context);
      return true;
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      this.value = this.value.replace(data.getString(context), "");
      return true;
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      return this.value.equals(data.getString(context));
   }

   @Override
   public boolean checkContainsAll(PolymorphicPlaceholder data, PlaceholderContext context) {
      return this.value.contains(data.getString(context));
   }

   @Override
   public String toString() {
      return this.value;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof StringVariable other)) {
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
      return other instanceof StringVariable;
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
