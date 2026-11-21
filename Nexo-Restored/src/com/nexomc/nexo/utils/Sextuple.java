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
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\b\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u0001*\u0006\b\u0001\u0010\u0002 \u0001*\u0006\b\u0002\u0010\u0003 \u0001*\u0006\b\u0003\u0010\u0004 \u0001*\u0006\b\u0004\u0010\u0005 \u0001*\u0006\b\u0005\u0010\u0006 \u00012\u00020\u0007B7\u0012\u0006\u0010\b\u001a\u00028\u0000\u0012\u0006\u0010\t\u001a\u00028\u0001\u0012\u0006\u0010\n\u001a\u00028\u0002\u0012\u0006\u0010\u000b\u001a\u00028\u0003\u0012\u0006\u0010\f\u001a\u00028\u0004\u0012\u0006\u0010\r\u001a\u00028\u0005¢\u0006\u0004\b\u000e\u0010\u000fJ\n\u0010\u0018\u001a\u00020\u0019H\u0096\u0080\u0004J\u000e\u0010\u001a\u001a\u00028\u0000HÆ\u0003¢\u0006\u0002\u0010\u0011J\u000e\u0010\u001b\u001a\u00028\u0001HÆ\u0003¢\u0006\u0002\u0010\u0011J\u000e\u0010\u001c\u001a\u00028\u0002HÆ\u0003¢\u0006\u0002\u0010\u0011J\u000e\u0010\u001d\u001a\u00028\u0003HÆ\u0003¢\u0006\u0002\u0010\u0011J\u000e\u0010\u001e\u001a\u00028\u0004HÆ\u0003¢\u0006\u0002\u0010\u0011J\u000e\u0010\u001f\u001a\u00028\u0005HÆ\u0003¢\u0006\u0002\u0010\u0011Jn\u0010 \u001a&\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u0004\u0012\u0004\u0012\u00028\u00050\u00002\b\b\u0002\u0010\b\u001a\u00028\u00002\b\b\u0002\u0010\t\u001a\u00028\u00012\b\b\u0002\u0010\n\u001a\u00028\u00022\b\b\u0002\u0010\u000b\u001a\u00028\u00032\b\b\u0002\u0010\f\u001a\u00028\u00042\b\b\u0002\u0010\r\u001a\u00028\u0005HÆ\u0001¢\u0006\u0002\u0010!J\u0014\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010%HÖ\u0083\u0004J\n\u0010&\u001a\u00020'HÖ\u0081\u0004R\u0013\u0010\b\u001a\u00028\u0000¢\u0006\n\n\u0002\u0010\u0012\u001a\u0004\b\u0010\u0010\u0011R\u0013\u0010\t\u001a\u00028\u0001¢\u0006\n\n\u0002\u0010\u0012\u001a\u0004\b\u0013\u0010\u0011R\u0013\u0010\n\u001a\u00028\u0002¢\u0006\n\n\u0002\u0010\u0012\u001a\u0004\b\u0014\u0010\u0011R\u0013\u0010\u000b\u001a\u00028\u0003¢\u0006\n\n\u0002\u0010\u0012\u001a\u0004\b\u0015\u0010\u0011R\u0013\u0010\f\u001a\u00028\u0004¢\u0006\n\n\u0002\u0010\u0012\u001a\u0004\b\u0016\u0010\u0011R\u0013\u0010\r\u001a\u00028\u0005¢\u0006\n\n\u0002\u0010\u0012\u001a\u0004\b\u0017\u0010\u0011¨\u0006("},
   d2 = {"Lcom/nexomc/nexo/utils/Sextuple;", "A", "B", "C", "D", "E", "F", "Ljava/io/Serializable;", "first", "second", "third", "fourth", "fifth", "sixth", "<init>", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", "getFirst", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getSecond", "getThird", "getFourth", "getFifth", "getSixth", "toString", "", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/nexomc/nexo/utils/Sextuple;", "equals", "", "other", "", "hashCode", "", "com.nexomc:core"}
)
public final class Sextuple<A, B, C, D, E, F> implements Serializable {
   private final A first;
   private final B second;
   private final C third;
   private final D fourth;
   private final E fifth;
   private final F sixth;

   public Sextuple(A first, B second, C third, D fourth, E fifth, F sixth) {
      this.first = (A)first;
      this.second = (B)second;
      this.third = (C)third;
      this.fourth = (D)fourth;
      this.fifth = (E)fifth;
      this.sixth = (F)sixth;
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

   public final E getFifth() {
      return this.fifth;
   }

   public final F getSixth() {
      return this.sixth;
   }

   @NotNull
   public String toString() {
      return "(" + this.first + ", " + this.second + ", " + this.third + ", " + this.fourth + ", " + this.fifth + ", " + this.sixth + ")";
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

   public final E component5() {
      return this.fifth;
   }

   public final F component6() {
      return this.sixth;
   }

   @NotNull
   public final Sextuple<A, B, C, D, E, F> copy(A first, B second, C third, D fourth, E fifth, F sixth) {
      return new Sextuple<A, B, C, D, E, F>(first, second, third, fourth, fifth, sixth);
   }

   public int hashCode() {
      int result = this.first == null ? 0 : this.first.hashCode();
      result = result * 31 + (this.second == null ? 0 : this.second.hashCode());
      result = result * 31 + (this.third == null ? 0 : this.third.hashCode());
      result = result * 31 + (this.fourth == null ? 0 : this.fourth.hashCode());
      result = result * 31 + (this.fifth == null ? 0 : this.fifth.hashCode());
      result = result * 31 + (this.sixth == null ? 0 : this.sixth.hashCode());
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Sextuple)) {
         return false;
      } else {
         Sextuple sextuple = (Sextuple)other;
         if (!Intrinsics.areEqual(this.first, sextuple.first)) {
            return false;
         } else if (!Intrinsics.areEqual(this.second, sextuple.second)) {
            return false;
         } else if (!Intrinsics.areEqual(this.third, sextuple.third)) {
            return false;
         } else if (!Intrinsics.areEqual(this.fourth, sextuple.fourth)) {
            return false;
         } else if (!Intrinsics.areEqual(this.fifth, sextuple.fifth)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.sixth, sextuple.sixth);
         }
      }
   }
}
