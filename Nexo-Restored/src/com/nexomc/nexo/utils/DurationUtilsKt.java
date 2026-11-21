package com.nexomc.nexo.utils;

import kotlin.Metadata;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationKt;
import kotlin.time.DurationUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u00000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\u001a\u0019\u0010\r\u001a\u00020\u000e*\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0004\b\u0011\u0010\u0012\u001a\u0017\u0010\u0013\u001a\u00020\u0001*\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0002\u0010\u0014\u001a\u0018\u0010\u0013\u001a\u0004\u0018\u00010\u0001*\u00020\u00172\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0010\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0006\"\u0015\u0010\u0000\u001a\u00020\u0002*\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b\"\u0015\u0010\t\u001a\u00020\u0002*\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\n\u0010\b\"\u0015\u0010\u000b\u001a\u00020\u0005*\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\f\u0010\u0006\"\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"},
   d2 = {"ticks", "Lkotlin/time/Duration;", "", "getTicks", "(I)J", "", "(J)J", "getTicks-LRDsOJo", "(J)I", "seconds", "getSeconds-LRDsOJo", "inWholeTicks", "getInWholeTicks-LRDsOJo", "toFloat", "", "unit", "Lkotlin/time/DurationUnit;", "toFloat-VtjQ1oo", "(JLkotlin/time/DurationUnit;)F", "toDuration", "(FLkotlin/time/DurationUnit;)J", "durationRegex", "Lkotlin/text/Regex;", "", "defaultUnit", "com.nexomc:core"}
)
public final class DurationUtilsKt {
   @NotNull
   private static final Regex durationRegex = new Regex("\\d+(?:\\.\\d+)?\\p{Alpha}+");

   public static final long getTicks(int value) {
      Duration.Companion companion = Duration.Companion;
      return DurationKt.toDuration(value * 50, DurationUnit.MILLISECONDS);
   }

   public static final long getTicks(long value) {
      Duration.Companion companion = Duration.Companion;
      return DurationKt.toDuration(value * (long)50, DurationUnit.MILLISECONDS);
   }

   public static final int getTicks_LRDsOJo/* $FF was: getTicks-LRDsOJo*/(long value) {
      return (int)getInWholeTicks_LRDsOJo(value);
   }

   public static final int getSeconds_LRDsOJo/* $FF was: getSeconds-LRDsOJo*/(long value) {
      return (int)getInWholeTicks_LRDsOJo(value);
   }

   public static final long getInWholeTicks_LRDsOJo/* $FF was: getInWholeTicks-LRDsOJo*/(long value) {
      return Duration.getInWholeMilliseconds_impl(value) / (long)50;
   }

   public static final float toFloat_VtjQ1oo/* $FF was: toFloat-VtjQ1oo*/(long value, @NotNull DurationUnit unit) {
      return (float)Duration.toDouble_impl(value, unit);
   }

   public static final long toDuration(float value, @NotNull DurationUnit unit) {
      return DurationKt.toDuration((double)value, unit);
   }

   @Nullable
   public static final Duration toDuration(@NotNull String str2, @Nullable DurationUnit defaultUnit) {
      Sequence matches = durationRegex.findAll((CharSequence)StringUtilsKt.removeSpaces(str2), 0);
      if (SequencesKt.none(matches)) {
         Double number = StringsKt.toDoubleOrNull(str2);
         Duration duration2;
         if (number != null) {
            double it = ((Number)number).doubleValue();
            int value2 = 0;
            if (defaultUnit != null) {
               int match = 0;
               duration2 = Duration.box_impl(DurationKt.toDuration(it, defaultUnit));
            } else {
               duration2 = null;
            }
         } else {
            duration2 = null;
         }

         return duration2;
      } else {
         Object initial = Duration.box_impl(Duration.Companion.getZERO_UwyO8pc());
         Object accumulator = initial;

         for(Object element : kotlin.sequences.SequencesKt.asIterable(matches)) {
            MatchResult matchResult = (MatchResult)element;
            long duration = ((Duration)accumulator).unbox_impl();
            int value3 = 0;
            String matchValue = matchResult.getValue();
            int endOfNumber = 0;

            while(endOfNumber < matchValue.length() && (Character.isDigit(matchValue.charAt(endOfNumber)) || matchValue.charAt(endOfNumber) == '.')) {
               ++endOfNumber;
            }

            String str = matchValue.substring(0, endOfNumber);
            double value = Double.parseDouble(str);
            String str3 = matchResult.getValue();
            int value8 = 0;
            int value4 = ((CharSequence)str3).length();

            while(true) {
               if (value8 >= value4) {
                  str = "";
                  break;
               }

               char it = str3.charAt(value8);
               int value5 = 0;
               if (!Character.isDigit(it) && it != '.') {
                  str = str3.substring(value8);
                  break;
               }

               ++value8;
            }

            Object obj;
            block: {
               switch (str) {
                  case "d":
                     Duration.Companion companion = Duration.Companion;
                     obj = Duration.box_impl(DurationKt.toDuration(value, DurationUnit.DAYS));
                     break block;
                  case "h":
                     Duration.Companion companion2 = Duration.Companion;
                     obj = Duration.box_impl(DurationKt.toDuration(value, DurationUnit.HOURS));
                     break block;
                  case "m":
                     Duration.Companion companion3 = Duration.Companion;
                     obj = Duration.box_impl(DurationKt.toDuration(value, DurationUnit.MINUTES));
                     break block;
                  case "s":
                     Duration.Companion companion4 = Duration.Companion;
                     obj = Duration.box_impl(DurationKt.toDuration(value, DurationUnit.SECONDS));
                     break block;
                  case "t":
                     obj = Duration.box_impl(getTicks((int)value));
                     break block;
                  case "w":
                     Duration.Companion companion5 = Duration.Companion;
                     obj = Duration.box_impl(Duration.times_UwyO8pc(DurationKt.toDuration(value, DurationUnit.DAYS), 7));
                     break block;
                  case "mo":
                     Duration.Companion companion6 = Duration.Companion;
                     obj = Duration.box_impl(Duration.times_UwyO8pc(DurationKt.toDuration(value, DurationUnit.DAYS), 31));
                     break block;
                  case "ms":
                     Duration.Companion companion7 = Duration.Companion;
                     obj = Duration.box_impl(DurationKt.toDuration(value, DurationUnit.MILLISECONDS));
                     break block;
               }

               if (defaultUnit != null) {
                  int value6 = 0;
                  obj = Duration.box_impl(DurationKt.toDuration(value, defaultUnit));
               } else {
                  obj = null;
               }
            }

            long value7;
            if (obj != null) {
               long add = ((Duration)obj).unbox_impl();
               value7 = Duration.plus_LRDsOJo(duration, add);
            } else {
               value7 = duration;
            }

            accumulator = Duration.box_impl(value7);
         }

         return (Duration)accumulator;
      }
   }
}
