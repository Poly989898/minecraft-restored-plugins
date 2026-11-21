package com.nexomc.nexo.utils;

import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000 \n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001\u001a\n\u0010\u0003\u001a\u00020\u0001*\u00020\u0001\u001a\u0012\u0010\u0004\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u0001\u001a\u0014\u0010\u0005\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0001H\u0000\u001a\u0014\u0010\u0007\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\b\u001a\u00020\u0001H\u0000\u001a\u001c\u0010\t\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\u0001H\u0000\u001a\u0016\u0010\f\u001a\u00020\r*\u00020\u00012\b\b\u0002\u0010\u000e\u001a\u00020\rH\u0000\u001a-\u0010\u000f\u001a\u0004\u0018\u0001H\u0010\"\u0004\b\u0000\u0010\u0010*\u00020\u00012\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u0002H\u00100\u0012H\u0000¢\u0006\u0002\u0010\u0013\u001a\u000e\u0010\u000f\u001a\u0004\u0018\u00010\u0001*\u00020\u0001H\u0000\u001a\u001c\u0010\u0014\u001a\u00020\u0001*\u0004\u0018\u00010\u00012\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u0016H\u0000\u001a\u0016\u0010\u0014\u001a\u00020\u0001*\u0004\u0018\u00010\u00012\u0006\u0010\u0015\u001a\u00020\u0001H\u0000¨\u0006\u0017"},
   d2 = {"joinToString", "", "separator", "removeSpaces", "remove", "appendIfMissing", "suffix", "prependIfMissing", "prefix", "substringBetween", "after", "before", "toIntRange", "Lkotlin/ranges/IntRange;", "default", "ifNotEmpty", "T", "block", "Lkotlin/Function1;", "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "ifNullOrEmpty", "value", "Lkotlin/Function0;", "com.nexomc:core"}
)
public final class StringUtilsKt {
   @NotNull
   public static final String joinToString(@NotNull String str, @NotNull String separator) {
      return CollectionsKt.joinToString((Iterable)StringsKt.toList((CharSequence)str), (CharSequence)separator, (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null);
   }

   @NotNull
   public static final String removeSpaces(@NotNull String str) {
      return StringsKt.replace(str, " ", "", false);
   }

   @NotNull
   public static final String remove(@NotNull String str, @NotNull String remove) {
      return StringsKt.replace(str, remove, "", false);
   }

   @NotNull
   public static final String appendIfMissing(@NotNull String str, @NotNull String suffix) {
      return StringsKt.endsWith(str, suffix, false) ? str : str + suffix;
   }

   @NotNull
   public static final String prependIfMissing(@NotNull String str, @NotNull String prefix) {
      return StringsKt.startsWith(str, prefix, false) ? str : prefix + str;
   }

   @NotNull
   public static final String substringBetween(@NotNull String str, @NotNull String after, @NotNull String before) {
      return StringsKt.substringBefore(StringsKt.substringAfter(str, after, str), before, StringsKt.substringAfter(str, after, str));
   }

   @NotNull
   public static final IntRange toIntRange(@NotNull String str, @NotNull IntRange intRange) {
      Integer number = StringsKt.toIntOrNull(StringsKt.substringBefore(str, "..", str));
      int first = number != null ? number : intRange.getFirst();
      Integer number2 = StringsKt.toIntOrNull(StringsKt.substringAfter(str, "..", str));
      return new IntRange(first, RangesKt.coerceAtLeast(number2 != null ? number2 : intRange.getLast(), first));
   }

   @Nullable
   public static final <T> T ifNotEmpty(@NotNull String str, @NotNull Function1<? super String, ? extends T> block) {
      return (T)(((CharSequence)str).length() > 0 ? block.invoke(str) : null);
   }

   @Nullable
   public static final String ifNotEmpty(@NotNull String str) {
      CharSequence charSequence = (CharSequence)str;
      CharSequence charSequence2;
      if (charSequence.length() == 0) {
         int value = 0;
         charSequence2 = null;
      } else {
         charSequence2 = charSequence;
      }

      return (String)charSequence2;
   }

   @NotNull
   public static final String ifNullOrEmpty(@Nullable String str, @NotNull Function0<String> value) {
      CharSequence charSequence = (CharSequence)str;
      return charSequence == null || charSequence.length() == 0 ? (String)value.invoke() : str;
   }

   @NotNull
   public static final String ifNullOrEmpty(@Nullable String str, @NotNull String value) {
      CharSequence charSequence = (CharSequence)str;
      return charSequence == null || charSequence.length() == 0 ? value : str;
   }
}
