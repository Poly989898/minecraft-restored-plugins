package io.lumine.mythic.core.skills.variables.types;

import com.google.common.base.Preconditions;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.interfaces.ContainingVariable;
import io.lumine.mythic.core.skills.variables.interfaces.NumericVariable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import lombok.Generated;
import lombok.Getter;

@Getter
public class TimeVariable extends Variable implements NumericVariable, ContainingVariable {
   private volatile Instant value;
   public static final VariableInfo<Instant> variableInfo = VariableInfo.<Instant>builder()
      .clazz(TimeVariable.class)
      .type(VariableType.TIME)
      .typeName("Time")
      .valueClass(Instant.class)
      .objectConstructor(TimeVariable::ofObject)
      .polymorphicConstructor(TimeVariable::of)
      .build();

   @Override
   public VariableInfo<Instant> getVariableInfo() {
      return variableInfo;
   }

   public static TimeVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new TimeVariable(data.getTime(context), expirationTime);
   }

   public static String toString(Instant data) {
      return data.toEpochMilli() + "";
   }

   public static Instant fromString(String data) {
      return Instant.ofEpochMilli(Long.parseLong(data));
   }

   public static TimeVariable ofObject(Object o, Long expirationTime) {
      if (o instanceof Instant instant) {
         return new TimeVariable(instant.toEpochMilli(), expirationTime);
      } else {
         return o instanceof Number number ? new TimeVariable(number.longValue(), expirationTime) : new TimeVariable(String.valueOf(o), expirationTime);
      }
   }

   public TimeVariable(String value) {
      this.build(value);
   }

   public TimeVariable(Instant value) {
      this.value = value;
   }

   public TimeVariable(String value, long expirationTime) {
      super(expirationTime);
      this.build(value);
   }

   public TimeVariable(Instant value, long expirationTime) {
      super(expirationTime);
      this.value = value;
   }

   public TimeVariable(long value, long expirationTime) {
      super(expirationTime);
      this.value = Instant.ofEpochMilli(value);
   }

   private void build(String value) {
      Preconditions.checkNotNull(value);
      this.value = fromString(value);
   }

   private static Instant getInstant(long time) {
      return Instant.ofEpochMilli(time);
   }

   public static String getFormattedTime(long time, String zoneOffset) {
      return getInstant(time).atOffset(ZoneOffset.of(zoneOffset)).toString();
   }

   public static long getTimeDelta(Instant time, Instant secondTime) {
      return time.until(secondTime, ChronoUnit.MILLIS);
   }

   public static String appendTime(long time, String unit) {
      if (time == 0L) {
         return "";
      } else {
         return time == 1L ? time + " " + unit + " " : time + " " + unit + "s ";
      }
   }

   public static String getFormattedDuration(long time) {
      long totalSeconds = time / 1000L;
      long secondsInYear = 31556951L;
      long secondsInMonth = 2630016L;
      long secondsInDay = 86400L;
      long secondsInHour = 3600L;
      long secondsInMinute = 60L;
      long years = totalSeconds / secondsInYear;
      totalSeconds %= secondsInYear;
      long months = totalSeconds / secondsInMonth;
      totalSeconds %= secondsInMonth;
      long days = totalSeconds / secondsInDay;
      totalSeconds %= secondsInDay;
      long hours = totalSeconds / secondsInHour;
      totalSeconds %= secondsInHour;
      long minutes = totalSeconds / secondsInMinute;
      long seconds = totalSeconds % secondsInMinute;
      String ret = "";
      ret = ret + appendTime(years, "Year");
      ret = ret + appendTime(months, "Month");
      ret = ret + appendTime(days, "Day");
      ret = ret + appendTime(hours, "Hour");
      ret = ret + appendTime(minutes, "Minute");
      return ret + appendTime(seconds, "Second");
   }

   public synchronized void setValue(Instant value) {
      this.value = value;
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      long amount = data.getTime(context).toEpochMilli();
      synchronized (this) {
         this.value = this.value.plusMillis(amount);
         return true;
      }
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      long amount = data.getTime(context).toEpochMilli();
      synchronized (this) {
         this.value = this.value.minusMillis(amount);
         return true;
      }
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      return this.value.toEpochMilli() == data.getTime(context).toEpochMilli();
   }

   @Override
   public boolean checkContainsAll(PolymorphicPlaceholder data, PlaceholderContext context) {
      return this.value.toEpochMilli() >= data.getTime(context).toEpochMilli();
   }

   public Instant get() {
      return this.value;
   }

   public Instant getValueClone() {
      return this.value;
   }

   @Override
   public String toString() {
      return String.valueOf(this.value.toEpochMilli());
   }

   @Override
   public String formatted() {
      return getFormattedDuration(this.value.toEpochMilli());
   }

   @Override
   public synchronized void executeMath(String eq) {
      this.value = Instant.ofEpochMilli((long)this.parseMath(eq, this.value.toEpochMilli()));
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TimeVariable other)) {
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
      return other instanceof TimeVariable;
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
