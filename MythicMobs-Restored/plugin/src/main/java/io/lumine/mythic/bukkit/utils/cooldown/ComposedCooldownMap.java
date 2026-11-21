package io.lumine.mythic.bukkit.utils.cooldown;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nonnull;

public interface ComposedCooldownMap<I, O> {
   @Nonnull
   static <I, O> ComposedCooldownMap<I, O> create(@Nonnull Cooldown base, @Nonnull Function<I, O> composeFunction) {
      Objects.requireNonNull(base, "base");
      Objects.requireNonNull(composeFunction, "composeFunction");
      return new ComposedCooldownMapImpl<>(base, composeFunction);
   }

   @Nonnull
   Cooldown getBase();

   @Nonnull
   Cooldown get(@Nonnull I i);

   void put(@Nonnull O o, @Nonnull Cooldown cooldown);

   @Nonnull
   Map<O, Cooldown> getAll();

   default boolean test(@Nonnull I key) {
      return this.get(key).test();
   }

   default boolean testSilently(@Nonnull I key) {
      return this.get(key).testSilently();
   }

   default long elapsed(@Nonnull I key) {
      return this.get(key).elapsed();
   }

   default void reset(@Nonnull I key) {
      this.get(key).reset();
   }

   default long remainingMillis(@Nonnull I key) {
      return this.get(key).remainingMillis();
   }

   default long remainingTime(@Nonnull I key, @Nonnull TimeUnit unit) {
      return this.get(key).remainingTime(unit);
   }

   @Nonnull
   default OptionalLong getLastTested(@Nonnull I key) {
      return this.get(key).getLastTested();
   }

   default void setLastTested(@Nonnull I key, long time) {
      this.get(key).setLastTested(time);
   }
}
