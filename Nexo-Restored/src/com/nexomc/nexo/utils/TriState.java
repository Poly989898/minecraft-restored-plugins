package com.nexomc.nexo.utils;

import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\f\b\u0086\u0081\u0002\u0018\u0000 \u00132\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0013B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u000f\u001a\u0004\u0018\u00010\b¢\u0006\u0002\u0010\u0010J\u000e\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\bR\u0011\u0010\u0007\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\tR\u0011\u0010\n\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\n\u0010\tR\u0011\u0010\u000b\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\f\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\f\u0010\tR\u0011\u0010\r\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\r\u0010\tR\u0011\u0010\u000e\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u000e\u0010\tj\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/utils/TriState;", "", "<init>", "(Ljava/lang/String;I)V", "NOT_SET", "TRUE", "FALSE", "isTrue", "", "()Z", "isNotTrue", "isFalse", "isNotFalse", "isSet", "isNotSet", "toBooleanOrNull", "()Ljava/lang/Boolean;", "toBoolean", "default", "Companion", "com.nexomc:core"}
)
public enum TriState {
   NOT_SET,
   TRUE,
   FALSE;

   @NotNull
   public static final Companion Companion = new Companion();

   public final boolean isTrue() {
      return this == TRUE;
   }

   public final boolean isNotTrue() {
      return this != TRUE;
   }

   public final boolean isFalse() {
      return this == FALSE;
   }

   public final boolean isNotFalse() {
      return this != FALSE;
   }

   public final boolean isSet() {
      return this != NOT_SET;
   }

   public final boolean isNotSet() {
      return this == NOT_SET;
   }

   @Nullable
   public final Boolean toBooleanOrNull() {
      return this.isTrue() ? Boolean.TRUE : (this.isFalse() ? Boolean.FALSE : null);
   }

   public final boolean toBoolean(boolean flag) {
      return this.isTrue() ? true : (this.isFalse() ? false : flag);
   }


   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0002\u0010\b¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/utils/TriState$Companion;", "", "<init>", "()V", "byBoolean", "Lcom/nexomc/nexo/utils/TriState;", "bool", "", "(Ljava/lang/Boolean;)Lcom/nexomc/nexo/utils/TriState;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final TriState byBoolean(@Nullable Boolean bool) {
         TriState triState;
         if (Intrinsics.areEqual(bool, true)) {
            triState = TriState.TRUE;
         } else if (Intrinsics.areEqual(bool, false)) {
            triState = TriState.FALSE;
         } else {
            if (bool != null) {
               throw new NoWhenBranchMatchedException();
            }

            triState = TriState.NOT_SET;
         }

         return triState;
      }
   }
}
