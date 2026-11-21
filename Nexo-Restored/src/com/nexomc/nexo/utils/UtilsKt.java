package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.configs.Settings;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.StringsKt;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\\\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000f\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u0004\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u0004\u0018\u0001H\u0001\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u0004\u0018\u00010\u0002H\u0086\b¢\u0006\u0002\u0010\u0003\u001a\u001c\u0010\u0004\u001a\u0002H\u0001\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u0004\u0018\u00010\u0002H\u0086\b¢\u0006\u0002\u0010\u0003\u001a)\u0010\u0005\u001a\u0004\u0018\u0001H\u0001\"\u000e\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0006*\u0002H\u00012\u0006\u0010\u0007\u001a\u0002H\u0001¢\u0006\u0002\u0010\b\u001a)\u0010\t\u001a\u0004\u0018\u0001H\u0001\"\u000e\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0006*\u0002H\u00012\u0006\u0010\n\u001a\u0002H\u0001¢\u0006\u0002\u0010\b\u001a)\u0010\u000b\u001a\u0004\u0018\u0001H\u0001\"\u000e\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0006*\u0002H\u00012\u0006\u0010\u0007\u001a\u0002H\u0001¢\u0006\u0002\u0010\b\u001a)\u0010\f\u001a\u0004\u0018\u0001H\u0001\"\u000e\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0006*\u0002H\u00012\u0006\u0010\n\u001a\u0002H\u0001¢\u0006\u0002\u0010\b\u001a+\u0010\r\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000e\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u0010¢\u0006\u0002\u0010\u0011\u001a=\u0010\u0012\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000e\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u000e2\u0006\u0010\u0013\u001a\u00020\u00102\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00170\u0015¢\u0006\u0002\u0010\u0018\u001a\u001f\u0010\u0019\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u0001*\u0002H\u00012\u0006\u0010\u0013\u001a\u00020\u0010¢\u0006\u0002\u0010\u0011\u001a\u001f\u0010\u001a\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u0001*\u0002H\u00012\u0006\u0010\u0013\u001a\u00020\u0010¢\u0006\u0002\u0010\u0011\u001a<\u0010\u001b\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u0002H\u00012\u0006\u0010\u0013\u001a\u00020\u00102\u0017\u0010\u001c\u001a\u0013\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00010\u0015¢\u0006\u0002\b\u001dH\u0086\bø\u0001\u0000¢\u0006\u0002\u0010\u0018\u001aH\u0010\u001b\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u0002H\u00012\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020\u00100\u00152\u0017\u0010\u001c\u001a\u0013\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00010\u0015¢\u0006\u0002\b\u001dH\u0086\bø\u0001\u0000¢\u0006\u0002\u0010\u001e\u001a<\u0010\u001f\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u0002H\u00012\u0006\u0010\u0013\u001a\u00020\u00102\u0017\u0010\u001c\u001a\u0013\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020\u00170\u0015¢\u0006\u0002\b\u001dH\u0086\bø\u0001\u0000¢\u0006\u0002\u0010\u0018\u001aH\u0010\u001f\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u0002H\u00012\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020\u00100\u00152\u0017\u0010\u001c\u001a\u0013\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020\u00170\u0015¢\u0006\u0002\b\u001dH\u0086\bø\u0001\u0000¢\u0006\u0002\u0010\u001e\u001a\u0010\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'H\u0000\u001a\u0010\u0010$\u001a\u00020%2\u0006\u0010(\u001a\u00020!H\u0000\u001a\u001a\u0010)\u001a\u00020!2\u0006\u0010*\u001a\u00020'2\b\b\u0002\u0010+\u001a\u00020!H\u0000\u001a\u001c\u0010,\u001a\u00020-*\u00020-2\u000e\u0010.\u001a\n\u0012\u0006\b\u0001\u0012\u00020-0/H\u0000\"\u0015\u0010 \u001a\u00020!*\u00020!8F¢\u0006\u0006\u001a\u0004\b\"\u0010#\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u00060"},
   d2 = {"safeCast", "T", "", "(Ljava/lang/Object;)Ljava/lang/Object;", "ensureCast", "atleast", "", "min", "(Ljava/lang/Comparable;Ljava/lang/Comparable;)Ljava/lang/Comparable;", "atmost", "max", "above", "below", "printOnFailure", "Lkotlin/Result;", "debugOnly", "", "(Ljava/lang/Object;Z)Ljava/lang/Object;", "runOrThrow", "condition", "block", "Lkotlin/Function1;", "", "", "(Ljava/lang/Object;ZLkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "takeIf", "takeUnless", "applyIf", "transform", "Lkotlin/ExtensionFunctionType;", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "applyAt", "radians", "", "getRadians", "(F)F", "floorToInt", "", "double", "", "float", "roundPredicate", "value", "step", "convertToTargetType", "", "targetType", "Ljava/lang/Class;", "com.nexomc:core"}
)
public final class UtilsKt {
   @Nullable
   public static final <T extends Comparable<? super T>> T atleast(@NotNull T t, @NotNull T min) {
      return (T)(t.compareTo(min) >= 0 ? t : null);
   }

   @Nullable
   public static final <T extends Comparable<? super T>> T atmost(@NotNull T t, @NotNull T max) {
      return (T)(t.compareTo(max) <= 0 ? t : null);
   }

   @Nullable
   public static final <T extends Comparable<? super T>> T above(@NotNull T t, @NotNull T min) {
      return (T)(t.compareTo(min) > 0 ? t : null);
   }

   @Nullable
   public static final <T extends Comparable<? super T>> T below(@NotNull T t, @NotNull T max) {
      return (T)(t.compareTo(max) < 0 ? t : null);
   }

   @NotNull
   public static final <T> Object printOnFailure(@NotNull Object obj, boolean debugOnly) {
      if (!debugOnly || Settings.DEBUG.toBool()) {
         Throwable throwable = ResultShim.exceptionOrNull(obj);
         if (throwable != null) {
            throwable.printStackTrace();
         }
      }

      return obj;
   }

   @NotNull
   public static final <T> Object runOrThrow(@NotNull Object obj, boolean condition, @NotNull Function1<? super Throwable, Unit> block) {
      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable throwable2 = throwable;
         int value = 0;
         throwable = condition ? throwable2 : null;
         if ((condition ? throwable2 : null) != null) {
            Throwable it = throwable;
            value = 0;
            block.invoke(it);
         }
      }

      return obj;
   }

   @Nullable
   public static final <T> T takeIf(T t, boolean condition) {
      return (T)(condition ? t : null);
   }

   @Nullable
   public static final <T> T takeUnless(T t, boolean condition) {
      return (T)(!condition ? t : null);
   }

   public static final <T> T applyIf(T t, boolean condition, @NotNull Function1<? super T, ? extends T> transform) {
      return (T)(condition ? transform.invoke(t) : t);
   }

   public static final <T> T applyIf(T t, @NotNull Function1<? super T, Boolean> condition, @NotNull Function1<? super T, ? extends T> transform) {
      return (T)((Boolean)condition.invoke(t) ? transform.invoke(t) : t);
   }

   public static final <T> T applyAt(T t, boolean condition, @NotNull Function1<? super T, Unit> transform) {
      if (condition) {
         transform.invoke(t);
      }

      return (T)t;
   }

   public static final <T> T applyAt(T t, @NotNull Function1<? super T, Boolean> condition, @NotNull Function1<? super T, Unit> transform) {
      if ((Boolean)condition.invoke(t)) {
         transform.invoke(t);
      }

      return (T)t;
   }

   public static final float getRadians(float value) {
      return (float)Math.toRadians((double)value);
   }

   public static final int floorToInt(double value) {
      return (int)Math.floor(value);
   }

   public static final int floorToInt(float value) {
      return (int)((float)Math.floor((double)value));
   }

   public static final float roundPredicate(double value, float step) {
      float roundedValue = (float)(Math.rint(value / (double)step) * (double)step);
      float remainder = (float)(value % (double)step);
      if (remainder > step / (float)2) {
         roundedValue += step;
      }

      StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
      String str = "%.2f";
      Object[] objs = new Object[]{roundedValue};
      String str2 = String.format(str, Arrays.copyOf(objs, objs.length));
      return Float.parseFloat(StringsKt.replace(str2, ",", ".", false));
   }

   @NotNull
   public static final Number convertToTargetType(@NotNull Number number2, @NotNull Class<? extends Number> targetType) {
      Number number;
      if (ClassUtils.isAssignable(targetType, Integer.class)) {
         number = (Number)number2.intValue();
      } else if (ClassUtils.isAssignable(targetType, Short.TYPE)) {
         number = (Number)number2.shortValue();
      } else if (ClassUtils.isAssignable(targetType, Double.TYPE)) {
         number = (Number)number2.doubleValue();
      } else if (ClassUtils.isAssignable(targetType, Float.TYPE)) {
         number = (Number)number2.floatValue();
      } else if (ClassUtils.isAssignable(targetType, Long.TYPE)) {
         number = (Number)number2.longValue();
      } else {
         if (!ClassUtils.isAssignable(targetType, Byte.TYPE)) {
            throw new IllegalArgumentException("Unsupported target numeric type: " + targetType);
         }

         number = (Number)number2.byteValue();
      }

      return number;
   }

   /** Kotlin default-argument bridge, kept for binary compatibility with classes that were not recompiled. */
   public static Object printOnFailure$default(Object result, boolean verbose, int mask, Object marker) {
      if ((mask & 1) != 0) verbose = false;
      return printOnFailure(result, verbose);
   }
}
