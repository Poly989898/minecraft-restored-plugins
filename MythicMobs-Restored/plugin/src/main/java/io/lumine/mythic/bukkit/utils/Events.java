package io.lumine.mythic.bukkit.utils;

import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.events.functional.merged.MergedSubscriptionBuilder;
import io.lumine.mythic.bukkit.utils.events.functional.single.SingleSubscriptionBuilder;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public final class Events {
   @Nonnull
   public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(@Nonnull Class<T> eventClass) {
      return SingleSubscriptionBuilder.newBuilder(eventClass);
   }

   @Nonnull
   public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(@Nonnull Class<T> eventClass, @Nonnull EventPriority priority) {
      return SingleSubscriptionBuilder.newBuilder(eventClass, priority);
   }

   @Nonnull
   public static <T> MergedSubscriptionBuilder<T> merge(@Nonnull Class<T> handledClass) {
      return MergedSubscriptionBuilder.newBuilder(handledClass);
   }

   @Nonnull
   public static <T> MergedSubscriptionBuilder<T> merge(@Nonnull TypeToken<T> type) {
      return MergedSubscriptionBuilder.newBuilder(type);
   }

   @Nonnull
   @SafeVarargs
   public static <S extends Event> MergedSubscriptionBuilder<S> merge(@Nonnull Class<S> superClass, @Nonnull Class<? extends S>... eventClasses) {
      return MergedSubscriptionBuilder.newBuilder(superClass, eventClasses);
   }

   @Nonnull
   @SafeVarargs
   public static <S extends Event> MergedSubscriptionBuilder<S> merge(
      @Nonnull Class<S> superClass, @Nonnull EventPriority priority, @Nonnull Class<? extends S>... eventClasses
   ) {
      return MergedSubscriptionBuilder.newBuilder(superClass, priority, eventClasses);
   }

   public static void call(@Nonnull Event event) {
      Bukkit.getPluginManager().callEvent(event);
   }

   public static void callAsync(@Nonnull Event event) {
      Schedulers.async().run(() -> call(event));
   }

   public static void callSync(@Nonnull Event event) {
      Schedulers.sync().run(() -> call(event));
   }

   public static void callSync(@Nonnull Entity entity, @Nonnull Event event) {
      if (ServerVersion.isFolia()) {
         Schedulers.of(entity).run(() -> call(event));
      } else {
         callSync(event);
      }
   }

   @Nonnull
   public static <T extends Event> T callSyncAndJoin(@Nonnull Entity entity, @Nonnull T event) {
      return ServerVersion.isFolia() ? Schedulers.of(entity).supply(() -> callAndReturn(event)).join() : callSyncAndJoin(event);
   }

   @Nonnull
   public static <T extends Event> T callAndReturn(@Nonnull T event) {
      Bukkit.getPluginManager().callEvent(event);
      return event;
   }

   @Nonnull
   public static <T extends Event> T callAsyncAndJoin(@Nonnull T event) {
      return Schedulers.async().supply(() -> callAndReturn(event)).join();
   }

   @Nonnull
   public static <T extends Event> T callSyncAndJoin(@Nonnull T event) {
      return Schedulers.sync().supply(() -> callAndReturn(event)).join();
   }

   private Events() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
