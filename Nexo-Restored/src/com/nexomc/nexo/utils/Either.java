package com.nexomc.nexo.utils;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u0001*\u0006\b\u0001\u0010\u0002 \u00012\u00020\u0003:\u0002\u0006\u0007B\t\b\u0004¢\u0006\u0004\b\u0004\u0010\u0005\u0082\u0001\u0002\b\t¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/utils/Either;", "A", "B", "", "<init>", "()V", "Left", "Right", "Lcom/nexomc/nexo/utils/Either$Left;", "Lcom/nexomc/nexo/utils/Either$Right;", "com.nexomc:core"}
)
public abstract sealed class Either<A, B> {
   private Either() {
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0001\n\u0002\b\u0007\u0018\u0000*\u0004\b\u0002\u0010\u00012\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020\u00030\u0002B\u000f\u0012\u0006\u0010\u0004\u001a\u00028\u0002¢\u0006\u0004\b\u0005\u0010\u0006R\u0013\u0010\u0004\u001a\u00028\u0002¢\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\b¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/utils/Either$Left;", "A", "Lcom/nexomc/nexo/utils/Either;", "", "value", "<init>", "(Ljava/lang/Object;)V", "getValue", "()Ljava/lang/Object;", "Ljava/lang/Object;", "com.nexomc:core"}
   )
   public static final class Left<A> extends Either {
      private final A value;

      public Left(A value) {
         super();
         this.value = (A)value;
      }

      public final A getValue() {
         return this.value;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0001\n\u0002\b\u0007\u0018\u0000*\u0004\b\u0002\u0010\u00012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u0002H\u00010\u0002B\u000f\u0012\u0006\u0010\u0004\u001a\u00028\u0002¢\u0006\u0004\b\u0005\u0010\u0006R\u0013\u0010\u0004\u001a\u00028\u0002¢\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\b¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/utils/Either$Right;", "B", "Lcom/nexomc/nexo/utils/Either;", "", "value", "<init>", "(Ljava/lang/Object;)V", "getValue", "()Ljava/lang/Object;", "Ljava/lang/Object;", "com.nexomc:core"}
   )
   public static final class Right<B> extends Either {
      private final B value;

      public Right(B value) {
         super();
         this.value = (B)value;
      }

      public final B getValue() {
         return this.value;
      }
   }
}
