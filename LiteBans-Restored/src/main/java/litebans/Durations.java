package litebans;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public final class Durations {
   private Durations() {
   }

   public final Regex obtainRegex() {
      return DurationUnit.obtainRegex();
   }

   public final Regex obtainRegex2() {
      return DurationUnit.obtainRegex2();
   }

   public final DurationUnit findUnit(@NotNull String text) {
      boolean flag = false;
      boolean flag2 = false;
      DurationUnit[] durationUnits = DurationUnit.values();
      int value = 0;
      int length2 = durationUnits.length;

      DurationUnit durationUnit;
      while (true) {
         if (value >= length2) {
            durationUnit = null;
            break;
         }

         DurationUnit durationUnit2;
         label36: {
            durationUnit2 = durationUnits[value];
            DurationUnit durationUnit3 = durationUnit2;
            if (!durationUnit3.checkText(text)) {
               String[] texts = DurationUnit.access$suffixes(durationUnit3);
               int value2 = 0;
               int length3 = texts.length;

               while (true) {
                  if (value2 >= length3) {
                     flag = false;
                     break;
                  }

                  String text2 = texts[value2];
                  if (StringsKt.checkText(text2, text, true)) {
                     flag = true;
                     break;
                  }

                  value2++;
               }

               if (!flag) {
                  flag2 = false;
                  break label36;
               }
            }

            flag2 = true;
         }

         if (flag2) {
            durationUnit = durationUnit2;
            break;
         }

         value++;
      }

      return durationUnit;
   }

   public final double parseDuration(@NotNull String text, double number) {
      if (!Intrinsics.areEqual(text, "permanent") && !Intrinsics.areEqual(text, "none") && !Intrinsics.areEqual(text, "unlimited") && !Intrinsics.areEqual(text, "never") && !Intrinsics.areEqual(text, Message.FOREVER.toString())) {
         List list;
         label57: {
            CharSequence message = text;
            char[] chars = new char[]{' '};
            List split = StringsKt.splitWithDefaults(message, chars, false, 0, 6, null);
            if (!split.isEmpty()) {
               ListIterator listIterator2 = split.listIterator(split.size());

               while (listIterator2.hasPrevious()) {
                  String previous2 = (String)listIterator2.previous();
                  if (previous2.length() != 0) {
                     list = CollectionsKt.take(split, listIterator2.nextIndex() + 1);
                     break label57;
                  }
               }
            }

            list = CollectionsKt.emptyList();
         }

         List list2 = list;
         if (list2.isEmpty()) {
            return number;
         }

         String text2 = StringsKt.computeText((String)list2.get(0), ',', '.', false, 4, null);
         Double double2 = StringsKt.computeDouble(text2);
         if (double2 != null) {
            String text3 = String.valueOf(StringsKt.computeCharValue2(text2));
            Intrinsics.checkNotNullExpressionValue(text3, "");
            if (!Intrinsics.areEqual(text3.toLowerCase(Locale.ROOT), "d")) {
               if (list2.size() == 1) {
                  return double2;
               }

               DurationUnit unit = this.findUnit((String)list2.get(1));
               if (unit == null) {
                  return number;
               }

               return unit.computeLongValue(double2);
            }
         }

         Double double3 = StringsKt.computeDouble(KotlinUtil9.computeText(this.obtainRegex2(), text2, ""));
         if (double3 != null) {
            double number2 = double3;
            String text4 = KotlinUtil9.computeText(this.obtainRegex(), text2, "");
            DurationUnit unit2 = this.findUnit(text4);
            if (unit2 == null) {
               return number;
            }

            return unit2.computeLongValue(number2);
         } else {
            return number;
         }
      } else {
         return 0.0;
      }
   }

   public static double parseDurationWithDefaults(Durations durations, String text, double number, int value, Object obj) {
      if ((value & 2) != 0) {
         number = 0.0;
      }

      return durations.parseDuration(text, number);
   }

   public final String formatDuration(long value, boolean flag) {
      if (value <= 0L) {
         return Message.FOREVER.toString();
      }

      long days = TimeUnit.MILLISECONDS.toDays(value);
      long hours = TimeUnit.MILLISECONDS.toHours(value);
      long value2 = hours - TimeUnit.DAYS.toHours(days);
      long value3 = TimeUnit.MILLISECONDS.toMinutes(value) - TimeUnit.HOURS.toMinutes(hours);
      long value4 = TimeUnit.MILLISECONDS.toSeconds(value) - TimeUnit.MINUTES.toSeconds(value3);
      CharSequence message;
      CharSequence message2;
      CharSequence message3;
      CharSequence message4;
      if (flag) {
         message = this.pluralize(days, Message.DAY, Message.DAYS);
         message2 = this.pluralize(value2, Message.HOUR, Message.HOURS);
         message3 = this.pluralize(value3, Message.MINUTE, Message.MINUTES);
         message4 = this.pluralize(value4, Message.SECOND, Message.SECONDS);
      } else {
         message = this.pluralize(days, "day", "days");
         message2 = this.pluralize(value2, "hour", "hours");
         message3 = this.pluralize(value3, "minute", "minutes");
         message4 = this.pluralize(value4, "second", "seconds");
      }

      String string = Message.DURATION_FORMAT.toString();
      String string2 = Message.DURATION_SEPARATOR.toString();
      String text;
      if (days <= 0L) {
         if (value2 <= 0L) {
            if (value3 <= 0L) {
               Object[] objs = new Object[]{value4, message4};
               text = String.format(string, Arrays.copyOf(objs, objs.length));
            } else {
               Object[] objs2 = new Object[]{value3, message3};
               text = String.format(string, Arrays.copyOf(objs2, objs2.length));
            }
         } else if (value3 == 0L) {
            Object[] objs3 = new Object[]{value2, message2};
            text = String.format(string, Arrays.copyOf(objs3, objs3.length));
         } else {
            String text2 = string + string2 + string;
            Object[] objs4 = new Object[]{value2, message2, value3, message3};
            text = String.format(text2, Arrays.copyOf(objs4, objs4.length));
         }
      } else if (value3 == 0L) {
         if (value2 == 0L) {
            Object[] objs5 = new Object[]{days, message};
            text = String.format(string, Arrays.copyOf(objs5, objs5.length));
         } else {
            String text3 = string + string2 + string;
            Object[] objs6 = new Object[]{days, message, value2, message2};
            text = String.format(text3, Arrays.copyOf(objs6, objs6.length));
         }
      } else if (value2 == 0L) {
         String text4 = string + string2 + string;
         Object[] objs7 = new Object[]{days, message, value3, message3};
         text = String.format(text4, Arrays.copyOf(objs7, objs7.length));
      } else {
         String text5 = string + string2 + string + string2 + string;
         Object[] objs8 = new Object[]{days, message, value2, message2, value3, message3};
         text = String.format(text5, Arrays.copyOf(objs8, objs8.length));
      }

      return text;
   }

   public static String formatDurationWithDefaults(Durations durations, long value, boolean flag, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         flag = true;
      }

      return durations.formatDuration(value, flag);
   }

   private final boolean equalsIgnoreCase(String text, Object obj) {
      return StringsKt.checkText(text, obj.toString(), true);
   }

   private final boolean equalsAnyIgnoreCase(String text, Object obj, Object obj2) {
      return this.equalsIgnoreCase(text, obj) || this.equalsIgnoreCase(text, obj2);
   }

   public final CharSequence pluralize(@NotNull Number number, @NotNull Object obj, @NotNull Object obj2) {
      return (number.doubleValue() == 1.0 ? obj : obj2).toString();
   }

   public static final boolean checkDurations(Durations durations, String text, Object obj, Object obj2) {
      return durations.equalsAnyIgnoreCase(text, obj, obj2);
   }

   public Durations(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
