package com.nexomc.customblockdata;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a\u001a\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0001¨\u0006\u0004"},
   d2 = {"substringBetween", "", "after", "before", "custom-block-data"}
)
public final class HelpersKt {
   @NotNull
   public static final String substringBetween(@NotNull String $this$substringBetween, @NotNull String after, @NotNull String before) {
      Intrinsics.checkNotNullParameter($this$substringBetween, "<this>");
      Intrinsics.checkNotNullParameter(after, "after");
      Intrinsics.checkNotNullParameter(before, "before");
   String s = $this$substringBetween;
   int i = s.indexOf(after);
   if (i >= 0) {
   s = s.substring(i + after.length());
   }
   int j = s.indexOf(before);
   if (j >= 0) {
   s = s.substring(0, j);
   }
   return s;
   }
}
