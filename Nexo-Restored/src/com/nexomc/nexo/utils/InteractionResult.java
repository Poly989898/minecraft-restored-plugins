package com.nexomc.nexo.utils;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\u0081\u0002\u0018\u0000 \r2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\rB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\t\u001a\u00020\nJ\u0006\u0010\u000b\u001a\u00020\nJ\u0006\u0010\f\u001a\u00020\nj\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/utils/InteractionResult;", "", "<init>", "(Ljava/lang/String;I)V", "SUCCESS", "CONSUME", "CONSUME_PARTIAL", "PASS", "FAIL", "consumesAction", "", "shouldSwing", "shouldAwardStats", "Companion", "com.nexomc:core"}
)
public enum InteractionResult {
   SUCCESS,
   CONSUME,
   CONSUME_PARTIAL,
   PASS,
   FAIL;

   @NotNull
   public static final Companion Companion = new Companion();

   public final boolean consumesAction() {
      return this == SUCCESS || this == CONSUME || this == CONSUME_PARTIAL;
   }

   public final boolean shouldSwing() {
      return this == SUCCESS;
   }

   public final boolean shouldAwardStats() {
      return this == SUCCESS || this == CONSUME;
   }


   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u0012\u0010\b\u001a\u00020\u00052\n\u0010\t\u001a\u0006\u0012\u0002\b\u00030\nJ\u0012\u0010\b\u001a\u00020\u00052\n\u0010\u000b\u001a\u0006\u0012\u0002\b\u00030\f¨\u0006\r"},
      d2 = {"Lcom/nexomc/nexo/utils/InteractionResult$Companion;", "", "<init>", "()V", "sidedSuccess", "Lcom/nexomc/nexo/utils/InteractionResult;", "swingHand", "", "fromNms", "nmsEnum", "", "nmsClass", "Ljava/lang/Class;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final InteractionResult sidedSuccess(boolean swingHand) {
         return swingHand ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      }

      @NotNull
      public final InteractionResult fromNms(@NotNull Enum<?> nmsEnum) {
         return InteractionResult.valueOf(nmsEnum.name());
      }

      @NotNull
      public final InteractionResult fromNms(@NotNull Class<?> nmsClass) {
         String name = nmsClass.getName();
         String str = name;
         Locale locale = Locale.getDefault();
         String str2 = str.toUpperCase(locale);
         return InteractionResult.valueOf(StringsKt.substringAfter(str2, "$", str2));
      }
   }
}
