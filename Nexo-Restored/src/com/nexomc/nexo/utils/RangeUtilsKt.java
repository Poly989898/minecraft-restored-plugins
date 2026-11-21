package com.nexomc.nexo.utils;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\n\u001a \u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u000e\u0010\u0005\u001a\u0004\u0018\u00010\u0006*\u00020\u0007H\u0000\u001a\u001c\u0010\b\u001a\u00020\u0006*\u00020\u00072\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u0004H\u0000\u001a\u0014\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0002\u0018\u00010\u0001*\u00020\u0007H\u0000\u001a\"\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u00072\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u0002H\u0000\u001a(\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u0002H\u0000\u001a\n\u0010\u0010\u001a\u00020\u0004*\u00020\u0006¨\u0006\u0011"},
   d2 = {"pow", "Lkotlin/ranges/ClosedFloatingPointRange;", "", "p", "", "toIntRangeOrNull", "Lkotlin/ranges/IntRange;", "", "toIntRangeOrDefault", "start", "end", "toFloatRangeOrNull", "toFloatRangeOrDefault", "coerceIn", "min", "max", "randomOrMin", "com.nexomc:core"}
)
public final class RangeUtilsKt {
   @NotNull
   public static final ClosedFloatingPointRange<Float> pow(@NotNull ClosedFloatingPointRange<Float> closedFloatingPointRange, int p) {
      return RangesKt.rangeTo((float)Math.pow((double)((Number)closedFloatingPointRange.getStart()).floatValue(), (double)2), (float)Math.pow((double)((Number)closedFloatingPointRange.getEndInclusive()).floatValue(), (double)2));
   }

   @Nullable
   public static final IntRange toIntRangeOrNull(@NotNull String str) {
        Intrinsics.checkNotNullParameter((Object)str, (String)"<this>");
        Integer n = StringsKt.toIntOrNull((String)StringsKt.substringBefore((String)str, (String)"..", (String)str));
        if (n == null) {
            return null;
        }
        int first = n;
        Integer n2 = StringsKt.toIntOrNull((String)StringsKt.substringAfter((String)str, (String)"..", (String)str));
        if (n2 == null) {
            return null;
        }
        int last = RangesKt.coerceAtLeast((int)n2, (int)first);
        return new IntRange(first, last);
    }

   @NotNull
   public static final IntRange toIntRangeOrDefault(@NotNull String str, int start, int end) {
        Intrinsics.checkNotNullParameter((Object)str, (String)"<this>");
        Integer n = StringsKt.toIntOrNull((String)StringsKt.substringBefore((String)str, (String)"..", (String)str));
        int first = n != null ? n : start;
        Integer n2 = StringsKt.toIntOrNull((String)StringsKt.substringAfter((String)str, (String)"..", (String)str));
        int last = n2 != null ? RangesKt.coerceAtLeast((int)n2, (int)first) : end;
        return new IntRange(first, last);
    }

   @Nullable
   public static final ClosedFloatingPointRange<Float> toFloatRangeOrNull(@NotNull String str) {
        Intrinsics.checkNotNullParameter((Object)str, (String)"<this>");
        Float f = StringsKt.toFloatOrNull((String)StringsKt.substringBefore((String)str, (String)"..", (String)str));
        if (f == null) {
            return null;
        }
        float first = f.floatValue();
        Float f2 = StringsKt.toFloatOrNull((String)StringsKt.substringAfter((String)str, (String)"..", (String)str));
        if (f2 == null) {
            return null;
        }
        float last = RangesKt.coerceAtLeast((float)f2.floatValue(), (float)first);
        return RangesKt.rangeTo((float)first, (float)last);
    }

   @NotNull
   public static final ClosedFloatingPointRange<Float> toFloatRangeOrDefault(@NotNull String str, float start, float end) {
        Intrinsics.checkNotNullParameter((Object)str, (String)"<this>");
        Float f = StringsKt.toFloatOrNull((String)StringsKt.substringBefore((String)str, (String)"..", (String)str));
        float first = f != null ? f.floatValue() : start;
        Float f2 = StringsKt.toFloatOrNull((String)StringsKt.substringAfter((String)str, (String)"..", (String)str));
        float last = f2 != null ? RangesKt.coerceAtLeast((float)f2.floatValue(), (float)first) : end;
        return RangesKt.rangeTo((float)first, (float)last);
    }

   @NotNull
   public static final ClosedFloatingPointRange<Float> coerceIn(@NotNull ClosedFloatingPointRange<Float> closedFloatingPointRange, float min, float max) {
      return RangesKt.rangeTo(RangesKt.coerceIn(((Number)closedFloatingPointRange.getStart()).floatValue(), min, max), RangesKt.coerceIn(((Number)closedFloatingPointRange.getEndInclusive()).floatValue(), min, max));
   }

   public static final int randomOrMin(@NotNull IntRange intRange) {
      return intRange.getStart() >= intRange.getEndInclusive() ? intRange.getStart() : Random.Default.nextInt(intRange.getStart(), intRange.getEndInclusive());
   }
}
