package com.nexomc.nexo.api;

import com.nexomc.nexo.decomp.ResultShim;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001\u001aB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J<\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0018\u0010\u000e\u001a\u0014\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100\u000fH\u0007b\u0002\b\u0011J\u0014\u0010\u0012\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007b\u0002\b\u0011J\u0014\u0010\u0013\u001a\u00020\u00142\u0006\u0010\t\u001a\u00020\nH\u0007b\u0002\b\u0011J\u0018\u0010\u0017\u001a\u00020\u00102\b\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0019\u001a\u00020\u0010R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0015\u001a\u00020\u00148F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016¨\u0006\u001b"},
   d2 = {"Lcom/nexomc/nexo/api/ComponentProjections;", "", "<init>", "()V", "projections", "", "Lcom/nexomc/nexo/api/ComponentProjections$Projection;", "register", "", "id", "", "appliesTo", "Ljava/util/function/Predicate;", "Lorg/bukkit/entity/Player;", "transform", "Ljava/util/function/BiFunction;", "Lnet/kyori/adventure/text/Component;", "Lkotlin/jvm/JvmStatic;", "unregister", "isRegistered", "", "isEmpty", "()Z", "apply", "player", "component", "Projection", "com.nexomc:core"}
)
public final class ComponentProjections {
   @NotNull
   public static final ComponentProjections INSTANCE = new ComponentProjections();
   @NotNull
   private static volatile List<Projection> projections = CollectionsKt.emptyList();

   private ComponentProjections() {
   }

   @JvmStatic
   public static final void register(@NotNull String id, @NotNull Predicate<Player> appliesTo, @NotNull BiFunction<Player, Component, Component> transform) {
      ComponentProjections componentProjections = INSTANCE;
      synchronized(componentProjections) {
         int value = 0;
         Iterable iterable = (Iterable)projections;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            Projection it = (Projection)element;
            int value2 = 0;
            if (!Intrinsics.areEqual(it.getId(), id)) {
               destination.add(element);
            }
         }

         projections = CollectionsKt.plus((Collection)((List)destination), new Projection(id, appliesTo, transform));
         Unit unit = Unit.INSTANCE;
      }
   }

   @JvmStatic
   public static final void unregister(@NotNull String id) {
      ComponentProjections componentProjections = INSTANCE;
      synchronized(componentProjections) {
         int value = 0;
         Iterable iterable = (Iterable)projections;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            Projection it = (Projection)element;
            int value2 = 0;
            if (!Intrinsics.areEqual(it.getId(), id)) {
               destination.add(element);
            }
         }

         projections = (List)destination;
         Unit unit = Unit.INSTANCE;
      }
   }

   @JvmStatic
   public static final boolean isRegistered(@NotNull String id) {
      Iterable iterable = (Iterable)projections;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            Projection it = (Projection)element;
            int value = 0;
            if (Intrinsics.areEqual(it.getId(), id)) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   public final boolean isEmpty() {
      return projections.isEmpty();
   }

   @NotNull
   public final Component apply(@Nullable Player player, @NotNull Component component) {
      java.lang.Object obj;
      List current = projections;
      if (!current.isEmpty() && player != null) {
         Object result = null;
         result = component;

         for(Projection projection : (java.lang.Iterable<Projection>)(java.lang.Iterable)current) {
            ComponentProjections componentProjections = this;

            Object obj2;
            try {
               ComponentProjections componentProjections2 = componentProjections;
               int value = 0;
               obj2 = ResultShim.ok(projection.getAppliesTo().test(player));
            } catch (Throwable e) {
               obj2 = ResultShim.ok(ResultKt.createFailure(e));
            }

            Object obj3 = obj2;
            Boolean flag = false;
            if ((Boolean)(ResultShim.isFailure(obj3) ? flag : obj3)) {
               ComponentProjections componentProjections3 = this;

               Object result2;
               try {
                  ComponentProjections componentProjections4 = componentProjections3;
                  int value2 = 0;
                  result2 = ResultShim.ok((Component)projection.getTransform().apply(player, (Component)result));
               } catch (Throwable ex) {
                  result2 = ResultShim.ok(ResultKt.createFailure(ex));
               }

               result = ResultShim.isFailure(result2) ? result : result2;
            }
         }

         return (Component)result;
      } else {
         return component;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\b\u0002\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0018\u0010\u0007\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\b¢\u0006\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR#\u0010\u0007\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u0012"},
      d2 = {"Lcom/nexomc/nexo/api/ComponentProjections$Projection;", "", "id", "", "appliesTo", "Ljava/util/function/Predicate;", "Lorg/bukkit/entity/Player;", "transform", "Ljava/util/function/BiFunction;", "Lnet/kyori/adventure/text/Component;", "<init>", "(Ljava/lang/String;Ljava/util/function/Predicate;Ljava/util/function/BiFunction;)V", "getId", "()Ljava/lang/String;", "getAppliesTo", "()Ljava/util/function/Predicate;", "getTransform", "()Ljava/util/function/BiFunction;", "com.nexomc:core"}
   )
   private static final class Projection {
      @NotNull
      private final String id;
      @NotNull
      private final Predicate<Player> appliesTo;
      @NotNull
      private final BiFunction<Player, Component, Component> transform;

      public Projection(@NotNull String id, @NotNull Predicate<Player> appliesTo, @NotNull BiFunction<Player, Component, Component> transform) {
         super();
         this.id = id;
         this.appliesTo = appliesTo;
         this.transform = transform;
      }

      @NotNull
      public final String getId() {
         return this.id;
      }

      @NotNull
      public final Predicate<Player> getAppliesTo() {
         return this.appliesTo;
      }

      @NotNull
      public final BiFunction<Player, Component, Component> getTransform() {
         return this.transform;
      }
   }
}
