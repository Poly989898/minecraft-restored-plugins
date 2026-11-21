package io.lumine.mythic.core.skills.variables.types;

import com.google.common.base.Preconditions;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import lombok.Generated;
import lombok.Getter;

@Getter
public class AbstractVariable extends Variable {
   private final Object value;
   public static final VariableInfo<Object> variableInfo = VariableInfo.<Object>builder()
      .clazz(AbstractVariable.class)
      .type(VariableType.ABSTRACT)
      .typeName("Abstract")
      .valueClass(Object.class)
      .objectConstructor(AbstractVariable::new)
      .polymorphicConstructor(null)
      .build();

   @Override
   public VariableInfo<Object> getVariableInfo() {
      return variableInfo;
   }

   public static String toString(Object data) {
      return String.valueOf(data);
   }

   public static Object fromString(String data) {
      return data;
   }

   public AbstractVariable(Object value) {
      Preconditions.checkNotNull(value);
      this.value = value;
   }

   public AbstractVariable(Object value, long time) {
      super(time);
      Preconditions.checkNotNull(value);
      this.value = value;
   }

   @Override
   public Object get() {
      return this.value;
   }

   @Override
   public Object getValueClone() {
      return this.value;
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      return false;
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      return false;
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      return false;
   }

   @Override
   public boolean shouldSave() {
      return false;
   }

   @Override
   public String toString() {
      return "VAR_Abstract(" + this.value.toString() + ")";
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractVariable other)) {
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
      return other instanceof AbstractVariable;
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
