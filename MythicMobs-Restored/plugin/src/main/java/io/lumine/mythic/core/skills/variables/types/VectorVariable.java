package io.lumine.mythic.core.skills.variables.types;

import com.google.common.base.Preconditions;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.serialization.serializers.VectorSerializer;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.interfaces.ContainingVariable;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Vector;

@Getter
@Setter
public class VectorVariable extends Variable implements ContainingVariable {
   private AbstractVector value;
   public static final VariableInfo<AbstractVector> variableInfo = VariableInfo.<AbstractVector>builder()
      .clazz(VectorVariable.class)
      .type(VariableType.VECTOR)
      .typeName("Vector")
      .valueClass(AbstractVector.class)
      .defaultValue(PolymorphicPlaceholder.of("0,0,0"))
      .objectConstructor(VectorVariable::ofObject)
      .polymorphicConstructor(VectorVariable::of)
      .build();

   @Override
   public VariableInfo<AbstractVector> getVariableInfo() {
      return variableInfo;
   }

   public static VectorVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new VectorVariable(data.getVector(context), expirationTime);
   }

   public static String toString(AbstractVector data) {
      return data.getX() + "," + data.getY() + "," + data.getZ();
   }

   public static AbstractVector fromString(String data) {
      return VectorSerializer.toVector(data);
   }

   public static String formatted(AbstractVector data) {
      return data.getX() + ", " + data.getY() + ", " + data.getZ();
   }

   public static VectorVariable ofObject(Object o, Long expirationTime) {
      if (o instanceof Vector vector) {
         return new VectorVariable(BukkitAdapter.adapt(vector), expirationTime);
      } else {
         return o instanceof AbstractVector vector ? new VectorVariable(vector, expirationTime) : new VectorVariable(String.valueOf(o), expirationTime);
      }
   }

   public VectorVariable(AbstractVector value) {
      this.build(value);
   }

   public VectorVariable(String rawData) {
      this.build(VectorSerializer.toVector(rawData));
   }

   public VectorVariable(AbstractVector value, long expirationTime) {
      super(expirationTime);
      this.build(value);
   }

   public VectorVariable(String rawData, long expirationTime) {
      super(expirationTime);
      this.build(VectorSerializer.toVector(rawData));
   }

   private void build(AbstractVector value) {
      Preconditions.checkNotNull(value);
      this.value = value;
   }

   public AbstractVector get() {
      return this.value;
   }

   public AbstractVector getValueClone() {
      return this.value.clone();
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      AbstractVector vec = data.getVector(context);
      this.value.add(vec);
      return true;
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      AbstractVector vec = data.getVector(context);
      this.value.subtract(vec);
      return true;
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      AbstractVector vec = data.getVector(context);
      return this.value.equals(vec);
   }

   @Override
   public boolean checkContainsAll(PolymorphicPlaceholder data, PlaceholderContext context) {
      AbstractVector vec = data.getVector(context);
      if (vec == null) {
         return false;
      } else {
         AbstractVector thisNormalized = this.value.clone().normalize();
         AbstractVector otherNormalized = vec.normalize();
         return thisNormalized.lengthSquared() == 0.0 && otherNormalized.lengthSquared() == 0.0 ? true : thisNormalized.equals(otherNormalized);
      }
   }

   @Override
   public String toString() {
      return toString(this.value);
   }

   @Override
   public Variable clone() {
      AbstractVector newVector = new AbstractVector(this.value.getX(), this.value.getY(), this.value.getZ());
      return new VectorVariable(newVector);
   }

   @Override
   public String formatted() {
      return this.value.getX() + ", " + this.value.getY() + ", " + this.value.getZ();
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof VectorVariable other)) {
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
      return other instanceof VectorVariable;
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
