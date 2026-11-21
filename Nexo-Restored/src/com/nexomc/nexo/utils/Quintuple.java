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
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\b\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u0001*\u0006\b\u0001\u0010\u0002 \u0001*\u0006\b\u0002\u0010\u0003 \u0001*\u0006\b\u0003\u0010\u0004 \u0001*\u0006\b\u0004\u0010\u0005 \u00012\u00020\u0006B/\u0012\u0006\u0010\u0007\u001a\u00028\u0000\u0012\u0006\u0010\b\u001a\u00028\u0001\u0012\u0006\u0010\t\u001a\u00028\u0002\u0012\u0006\u0010\n\u001a\u00028\u0003\u0012\u0006\u0010\u000b\u001a\u00028\u0004¢\u0006\u0004\b\f\u0010\rJ\n\u0010\u0015\u001a\u00020\u0016H\u0096\u0080\u0004J\u000e\u0010\u0017\u001a\u00028\u0000HÆ\u0003¢\u0006\u0002\u0010\u000fJ\u000e\u0010\u0018\u001a\u00028\u0001HÆ\u0003¢\u0006\u0002\u0010\u000fJ\u000e\u0010\u0019\u001a\u00028\u0002HÆ\u0003¢\u0006\u0002\u0010\u000fJ\u000e\u0010\u001a\u001a\u00028\u0003HÆ\u0003¢\u0006\u0002\u0010\u000fJ\u000e\u0010\u001b\u001a\u00028\u0004HÆ\u0003¢\u0006\u0002\u0010\u000fJ^\u0010\u001c\u001a \u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u00040\u00002\b\b\u0002\u0010\u0007\u001a\u00028\u00002\b\b\u0002\u0010\b\u001a\u00028\u00012\b\b\u0002\u0010\t\u001a\u00028\u00022\b\b\u0002\u0010\n\u001a\u00028\u00032\b\b\u0002\u0010\u000b\u001a\u00028\u0004HÆ\u0001¢\u0006\u0002\u0010\u001dJ\u0014\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010!HÖ\u0083\u0004J\n\u0010\"\u001a\u00020#HÖ\u0081\u0004R\u0013\u0010\u0007\u001a\u00028\u0000¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\b\u001a\u00028\u0001¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0011\u0010\u000fR\u0013\u0010\t\u001a\u00028\u0002¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0012\u0010\u000fR\u0013\u0010\n\u001a\u00028\u0003¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0013\u0010\u000fR\u0013\u0010\u000b\u001a\u00028\u0004¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0014\u0010\u000f¨\u0006$"},
   d2 = {"Lcom/nexomc/nexo/utils/Quintuple;", "A", "B", "C", "D", "E", "Ljava/io/Serializable;", "first", "second", "third", "fourth", "fifth", "<init>", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", "getFirst", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getSecond", "getThird", "getFourth", "getFifth", "toString", "", "component1", "component2", "component3", "component4", "component5", "copy", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/nexomc/nexo/utils/Quintuple;", "equals", "", "other", "", "hashCode", "", "com.nexomc:core"}
)
public final class Quintuple<A, B, C, D, E> implements Serializable {
   private final A first;
   private final B second;
   private final C third;
   private final D fourth;
   private final E fifth;

   public Quintuple(A first, B second, C third, D fourth, E fifth) {
      this.first = (A)first;
      this.second = (B)second;
      this.third = (C)third;
      this.fourth = (D)fourth;
      this.fifth = (E)fifth;
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

   @NotNull
   public String toString() {
      return "(" + this.first + ", " + this.second + ", " + this.third + ", " + this.fourth + ", " + this.fifth + ")";
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

   @NotNull
   public final Quintuple<A, B, C, D, E> copy(A first, B second, C third, D fourth, E fifth) {
      return new Quintuple<A, B, C, D, E>(first, second, third, fourth, fifth);
   }

   public int hashCode() {
      int result = this.first == null ? 0 : this.first.hashCode();
      result = result * 31 + (this.second == null ? 0 : this.second.hashCode());
      result = result * 31 + (this.third == null ? 0 : this.third.hashCode());
      result = result * 31 + (this.fourth == null ? 0 : this.fourth.hashCode());
      result = result * 31 + (this.fifth == null ? 0 : this.fifth.hashCode());
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Quintuple)) {
         return false;
      } else {
         Quintuple quintuple = (Quintuple)other;
         if (!Intrinsics.areEqual(this.first, quintuple.first)) {
            return false;
         } else if (!Intrinsics.areEqual(this.second, quintuple.second)) {
            return false;
         } else if (!Intrinsics.areEqual(this.third, quintuple.third)) {
            return false;
         } else if (!Intrinsics.areEqual(this.fourth, quintuple.fourth)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.fifth, quintuple.fifth);
         }
      }
   }
}
