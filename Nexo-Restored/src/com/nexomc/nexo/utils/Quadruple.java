package com.nexomc.nexo.utils;

import java.io.Serializable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\b\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u0001*\u0006\b\u0001\u0010\u0002 \u0001*\u0006\b\u0002\u0010\u0003 \u0001*\u0006\b\u0003\u0010\u0004 \u00012\u00020\u0005B'\u0012\u0006\u0010\u0006\u001a\u00028\u0000\u0012\u0006\u0010\u0007\u001a\u00028\u0001\u0012\u0006\u0010\b\u001a\u00028\u0002\u0012\u0006\u0010\t\u001a\u00028\u0003¢\u0006\u0004\b\n\u0010\u000bJ\n\u0010\u0012\u001a\u00020\u0013H\u0096\u0080\u0004J\u000e\u0010\u0014\u001a\u00028\u0000HÆ\u0003¢\u0006\u0002\u0010\rJ\u000e\u0010\u0015\u001a\u00028\u0001HÆ\u0003¢\u0006\u0002\u0010\rJ\u000e\u0010\u0016\u001a\u00028\u0002HÆ\u0003¢\u0006\u0002\u0010\rJ\u000e\u0010\u0017\u001a\u00028\u0003HÆ\u0003¢\u0006\u0002\u0010\rJN\u0010\u0018\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u00002\b\b\u0002\u0010\u0006\u001a\u00028\u00002\b\b\u0002\u0010\u0007\u001a\u00028\u00012\b\b\u0002\u0010\b\u001a\u00028\u00022\b\b\u0002\u0010\t\u001a\u00028\u0003HÆ\u0001¢\u0006\u0002\u0010\u0019J\u0014\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dHÖ\u0083\u0004J\n\u0010\u001e\u001a\u00020\u001fHÖ\u0081\u0004R\u0013\u0010\u0006\u001a\u00028\u0000¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0007\u001a\u00028\u0001¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\u000f\u0010\rR\u0013\u0010\b\u001a\u00028\u0002¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\u0010\u0010\rR\u0013\u0010\t\u001a\u00028\u0003¢\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\u0011\u0010\r¨\u0006 "},
   d2 = {"Lcom/nexomc/nexo/utils/Quadruple;", "A", "B", "C", "D", "Ljava/io/Serializable;", "first", "second", "third", "fourth", "<init>", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", "getFirst", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getSecond", "getThird", "getFourth", "toString", "", "component1", "component2", "component3", "component4", "copy", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/nexomc/nexo/utils/Quadruple;", "equals", "", "other", "", "hashCode", "", "com.nexomc:core"}
)
public final class Quadruple<A, B, C, D> implements Serializable {
   private final A first;
   private final B second;
   private final C third;
   private final D fourth;

   public Quadruple(A first, B second, C third, D fourth) {
      this.first = (A)first;
      this.second = (B)second;
      this.third = (C)third;
      this.fourth = (D)fourth;
   }

   public final A getFirst() {
      return this.first;
   }

   public final B getSecond() {
      return this.second;
   }

   public final C getThird() {
      return this.third;
   }

   public final D getFourth() {
      return this.fourth;
   }

   @NotNull
   public String toString() {
      return "(" + this.first + ", " + this.second + ", " + this.third + ", " + this.fourth + ")";
   }

   public final A component1() {
      return this.first;
   }

   public final B component2() {
      return this.second;
   }

   public final C component3() {
      return this.third;
   }

   public final D component4() {
      return this.fourth;
   }

   @NotNull
   public final Quadruple<A, B, C, D> copy(A first, B second, C third, D fourth) {
      return new Quadruple<A, B, C, D>(first, second, third, fourth);
   }

   public int hashCode() {
      int result = this.first == null ? 0 : this.first.hashCode();
      result = result * 31 + (this.second == null ? 0 : this.second.hashCode());
      result = result * 31 + (this.third == null ? 0 : this.third.hashCode());
      result = result * 31 + (this.fourth == null ? 0 : this.fourth.hashCode());
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Quadruple)) {
         return false;
      } else {
         Quadruple quadruple = (Quadruple)other;
         if (!Intrinsics.areEqual(this.first, quadruple.first)) {
            return false;
         } else if (!Intrinsics.areEqual(this.second, quadruple.second)) {
            return false;
         } else if (!Intrinsics.areEqual(this.third, quadruple.third)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.fourth, quadruple.fourth);
         }
      }
   }
}
