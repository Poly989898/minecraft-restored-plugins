package litebans;

import org.jetbrains.annotations.NotNull;

public enum DurationUnit {
   SECONDS(new String[]{"s", "se", "sc", "sec", "secs", "second", "seconds"}),
   MINUTES(new String[]{"m", "mi", "min", "mins", "minute", "minutes"}),
   HOURS(new String[]{"h", "hr", "hrs", "hour", "hours"}),
   DAYS(new String[]{"d", "da", "dy", "day", "days"}),
   WEEKS(new String[]{"w", "we", "wk", "wks", "week", "weeks"}),
   MONTHS(new String[]{"mo", "mh", "mon", "month", "months"}),
   YEARS(new String[]{"y", "ye", "yr", "yrs", "year", "years"});

   public static final Durations DURATIONS = new Durations(null);
   private final String[] suffixes;
   public static final double DOUBLE_VALUE = 0.0;
   private static final Regex DIGITS = new Regex("[0-9.]");
   private static final Regex NON_DIGITS = new Regex("[^0-9.]");
   private static final KotlinListSub KOTLIN_LIST_SUB = EnumEntries.computeKotlinListSub(values());

   DurationUnit(String[] texts) {
      this.suffixes = texts;
   }

   public final long computeLongValue(double number) {
      double number2 = number * 1000.0;
      double number3;
      switch (DurationUnitSwitchMap.value[this.ordinal()]) {
         case 1:
            number3 = number2;
            break;
         case 2:
            number3 = number2 * 60.0;
            break;
         case 3:
            number3 = number2 * 60.0 * 60.0;
            break;
         case 4:
            number3 = number2 * 60.0 * 60.0 * 24.0;
            break;
         case 5:
            number3 = number2 * 60.0 * 60.0 * 24.0 * 7.0;
            break;
         case 6:
            number3 = number2 * 60.0 * 60.0 * 24.0 * 30.0;
            break;
         case 7:
            number3 = number2 * 60.0 * 60.0 * 24.0 * 365.0;
            break;
         default:
            throw new KotlinRuntimeExceptionSub();
      }

      return (long)number3;
   }

   public final boolean checkText(@NotNull String text) {
      boolean flag;
      switch (DurationUnitSwitchMap.value[this.ordinal()]) {
         case 1:
            flag = Durations.checkDurations(DURATIONS, text, Message.SECOND, Message.SECONDS);
            break;
         case 2:
            flag = Durations.checkDurations(DURATIONS, text, Message.MINUTE, Message.MINUTES);
            break;
         case 3:
            flag = Durations.checkDurations(DURATIONS, text, Message.HOUR, Message.HOURS);
            break;
         case 4:
            flag = Durations.checkDurations(DURATIONS, text, Message.DAY, Message.DAYS);
            break;
         case 5:
            flag = Durations.checkDurations(DURATIONS, text, Message.WEEK, Message.WEEKS);
            break;
         case 6:
            flag = Durations.checkDurations(DURATIONS, text, Message.MONTH, Message.MONTHS);
            break;
         case 7:
            flag = Durations.checkDurations(DURATIONS, text, Message.YEAR, Message.YEARS);
            break;
         default:
            throw new KotlinRuntimeExceptionSub();
      }

      return flag;
   }

   public final CharSequence computeMessage(double number) {
      CharSequence message;
      switch (DurationUnitSwitchMap.value[this.ordinal()]) {
         case 1:
            message = DURATIONS.pluralize(number, Message.SECOND, Message.SECONDS);
            break;
         case 2:
            message = DURATIONS.pluralize(number, Message.MINUTE, Message.MINUTES);
            break;
         case 3:
            message = DURATIONS.pluralize(number, Message.HOUR, Message.HOURS);
            break;
         case 4:
            message = DURATIONS.pluralize(number, Message.DAY, Message.DAYS);
            break;
         case 5:
            message = DURATIONS.pluralize(number, Message.WEEK, Message.WEEKS);
            break;
         case 6:
            message = DURATIONS.pluralize(number, Message.MONTH, Message.MONTHS);
            break;
         case 7:
            message = DURATIONS.pluralize(number, Message.YEAR, Message.YEARS);
            break;
         default:
            throw new KotlinRuntimeExceptionSub();
      }

      return message;
   }

   public static DurationUnit computeDurationUnit(String text) {
      return Enum.valueOf(DurationUnit.class, text);
   }

   private static final DurationUnit[] obtainDurationUnitArray() {
      return new DurationUnit[]{SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS};
   }

   public static final String[] access$suffixes(DurationUnit durationUnit) {
      return durationUnit.suffixes;
   }

   public static final Regex obtainRegex() {
      return DIGITS;
   }

   public static final Regex obtainRegex2() {
      return NON_DIGITS;
   }

}
