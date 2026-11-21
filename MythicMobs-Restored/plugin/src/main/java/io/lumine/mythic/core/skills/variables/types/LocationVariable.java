package io.lumine.mythic.core.skills.variables.types;

import com.google.common.base.Preconditions;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class LocationVariable extends Variable {
   private AbstractLocation value;
   public static final VariableInfo<AbstractLocation> variableInfo = VariableInfo.<AbstractLocation>builder()
      .clazz(LocationVariable.class)
      .type(VariableType.LOCATION)
      .typeName("Location")
      .valueClass(AbstractLocation.class)
      .objectConstructor(LocationVariable::ofObject)
      .polymorphicConstructor(LocationVariable::of)
      .build();

   @Override
   public VariableInfo<AbstractLocation> getVariableInfo() {
      return variableInfo;
   }

   public static LocationVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new LocationVariable(data.getString(context), expirationTime);
   }

   public static String toString(AbstractLocation data) {
      return data.getWorld().getName() + "," + data.getX() + "," + data.getY() + "," + data.getZ();
   }

   public static AbstractLocation fromString(String data) {
      return AbstractLocation.deserializeFromVariableLocation(data);
   }

   public static String formatted(AbstractLocation data) {
      return data.getWorld() + ", " + data.getX() + ", " + data.getY() + ", " + data.getZ();
   }

   public static LocationVariable ofObject(Object o, Long expirationTime) {
      if (o instanceof AbstractLocation loc) {
         return new LocationVariable(loc, expirationTime);
      } else {
         return o instanceof Location loc
            ? new LocationVariable(BukkitAdapter.adapt(loc), expirationTime)
            : new LocationVariable(String.valueOf(o), expirationTime);
      }
   }

   public LocationVariable(AbstractLocation value) {
      Preconditions.checkNotNull(value);
      this.value = value;
   }

   public LocationVariable(AbstractLocation value, long expirationTime) {
      super(expirationTime);
      Preconditions.checkNotNull(value);
      this.value = value;
   }

   public LocationVariable(String value) {
      this(fromString(value));
   }

   public LocationVariable(String value, long expirationTime) {
      this(AbstractLocation.deserializeFromVariableLocation(value), expirationTime);
   }

   public AbstractLocation get() {
      return this.value;
   }

   public AbstractLocation getValueClone() {
      return this.value.clone();
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      String processedData = data.getString(context);
      List<String> split = Arrays.asList(processedData.trim().split(","));
      if (split.size() == 4) {
         split.remove(0);
      }

      if (split.size() == 3) {
         try {
            double x = Double.parseDouble(split.get(0).trim());
            double y = Double.parseDouble(split.get(1).trim());
            double z = Double.parseDouble(split.get(2).trim());
            this.value.setX(this.value.getX() + x);
            this.value.setY(this.value.getY() + y);
            this.value.setZ(this.value.getZ() + z);
            return true;
         } catch (NumberFormatException e) {
            MythicLogger.handleMinorError(e);
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      String processedData = data.getString(context);
      String[] split = processedData.trim().split(",");
      if (split.length == 3) {
         try {
            double x = Double.parseDouble(split[0].trim());
            double y = Double.parseDouble(split[1].trim());
            double z = Double.parseDouble(split[2].trim());
            this.value.setX(this.value.getX() - x);
            this.value.setY(this.value.getY() - y);
            this.value.setZ(this.value.getZ() - z);
            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      AbstractLocation newLocation = AbstractLocation.deserializeFromVariableLocation(data.getString(context));
      return this.value.equals(newLocation);
   }

   @Override
   public String toString() {
      return toString(this.value);
   }

   @Override
   public Variable clone() {
      AbstractLocation newLocation = new AbstractLocation(this.value.getWorld(), this.value.getX(), this.value.getY(), this.value.getZ());
      return new LocationVariable(newLocation);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof LocationVariable other)) {
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
      return other instanceof LocationVariable;
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
