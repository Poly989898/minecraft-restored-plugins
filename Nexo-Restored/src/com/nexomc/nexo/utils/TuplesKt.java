package com.nexomc.nexo.utils;

import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Triple;
import kotlin.collections.CollectionsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u00002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\u001aJ\u0010\u0000\u001a\u0014\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u00040\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u0004*\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00030\u00052\u0006\u0010\u0006\u001a\u0002H\u0004H\u0086\u0004¢\u0006\u0002\u0010\u0007\u001a\\\u0010\u0000\u001a\u001a\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\t0\b\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u0004\"\u0004\b\u0003\u0010\t*\u0014\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u00040\u00012\u0006\u0010\u0006\u001a\u0002H\tH\u0086\u0004¢\u0006\u0002\u0010\n\u001an\u0010\u0000\u001a \u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\t\u0012\u0004\u0012\u0002H\f0\u000b\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u0004\"\u0004\b\u0003\u0010\t\"\u0004\b\u0004\u0010\f*\u001a\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\t0\b2\u0006\u0010\u0006\u001a\u0002H\fH\u0086\u0004¢\u0006\u0002\u0010\r\u001a\u0080\u0001\u0010\u0000\u001a&\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\t\u0012\u0004\u0012\u0002H\f\u0012\u0004\u0012\u0002H\u000f0\u000e\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u0004\"\u0004\b\u0003\u0010\t\"\u0004\b\u0004\u0010\f\"\u0004\b\u0005\u0010\u000f* \u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\t\u0012\u0004\u0012\u0002H\f0\u000b2\u0006\u0010\u0006\u001a\u0002H\u000fH\u0086\u0004¢\u0006\u0002\u0010\u0010\u001a.\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00130\u0012\"\u0004\b\u0000\u0010\u0013*\u001a\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u00130\b\u001a4\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00130\u0012\"\u0004\b\u0000\u0010\u0013* \u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u00130\u000b\u001a:\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00130\u0012\"\u0004\b\u0000\u0010\u0013*&\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u0002H\u00130\u000e¨\u0006\u0014"},
   d2 = {"to", "Lkotlin/Triple;", "A", "B", "C", "Lkotlin/Pair;", "that", "(Lkotlin/Pair;Ljava/lang/Object;)Lkotlin/Triple;", "Lcom/nexomc/nexo/utils/Quadruple;", "D", "(Lkotlin/Triple;Ljava/lang/Object;)Lcom/nexomc/nexo/utils/Quadruple;", "Lcom/nexomc/nexo/utils/Quintuple;", "E", "(Lcom/nexomc/nexo/utils/Quadruple;Ljava/lang/Object;)Lcom/nexomc/nexo/utils/Quintuple;", "Lcom/nexomc/nexo/utils/Sextuple;", "F", "(Lcom/nexomc/nexo/utils/Quintuple;Ljava/lang/Object;)Lcom/nexomc/nexo/utils/Sextuple;", "toList", "", "T", "com.nexomc:core"}
)
public final class TuplesKt {
   @NotNull
   public static final <A, B, C> Triple<A, B, C> to(@NotNull Pair<? extends A, ? extends B> pair, C that) {
      return new Triple(pair.getFirst(), pair.getSecond(), that);
   }

   @NotNull
   public static final <A, B, C, D> Quadruple<A, B, C, D> to(@NotNull Triple<? extends A, ? extends B, ? extends C> triple, D that) {
      return new Quadruple<A, B, C, D>(triple.getFirst(), triple.getSecond(), triple.getThird(), that);
   }

   @NotNull
   public static final <A, B, C, D, E> Quintuple<A, B, C, D, E> to(@NotNull Quadruple<? extends A, ? extends B, ? extends C, ? extends D> quadruple, E that) {
      return new Quintuple<A, B, C, D, E>(quadruple.getFirst(), quadruple.getSecond(), quadruple.getThird(), quadruple.getFourth(), that);
   }

   @NotNull
   public static final <A, B, C, D, E, F> Sextuple<A, B, C, D, E, F> to(@NotNull Quintuple<? extends A, ? extends B, ? extends C, ? extends D, ? extends E> quintuple, F that) {
      return new Sextuple<A, B, C, D, E, F>(quintuple.getFirst(), quintuple.getSecond(), quintuple.getThird(), quintuple.getFourth(), quintuple.getFifth(), that);
   }

   @NotNull
   public static final <T> List<T> toList(@NotNull Quadruple<? extends T, ? extends T, ? extends T, ? extends T> quadruple) {
      Object[] objs = new Object[]{quadruple.getFirst(), quadruple.getSecond(), quadruple.getThird(), quadruple.getFourth()};
      return (List<T>)(List)CollectionsKt.listOf(objs);
   }

   @NotNull
   public static final <T> List<T> toList(@NotNull Quintuple<? extends T, ? extends T, ? extends T, ? extends T, ? extends T> quintuple) {
      Object[] objs = new Object[]{quintuple.getFirst(), quintuple.getSecond(), quintuple.getThird(), quintuple.getFourth(), quintuple.getFifth()};
      return (List<T>)(List)CollectionsKt.listOf(objs);
   }

   @NotNull
   public static final <T> List<T> toList(@NotNull Sextuple<? extends T, ? extends T, ? extends T, ? extends T, ? extends T, ? extends T> sextuple) {
      Object[] objs = new Object[]{sextuple.getFirst(), sextuple.getSecond(), sextuple.getThird(), sextuple.getFourth(), sextuple.getFifth(), sextuple.getSixth()};
      return (List<T>)(List)CollectionsKt.listOf(objs);
   }
}
