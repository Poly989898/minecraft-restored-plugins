package io.lumine.mythic.bukkit.utils.cooldown;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public interface CooldownMap<T> {
   @Nonnull
   static <T> CooldownMap<T> create(@Nonnull Cooldown base) {
      Objects.requireNonNull(base, "base");
      return new CooldownMapImpl<>(base);
   }

   @Nonnull
   Cooldown getBase();

   @Nonnull
   Cooldown get(@Nonnull T t);

   void put(@Nonnull T t, @Nonnull Cooldown cooldown);

   @Nonnull
   Map<T, Cooldown> getAll();

   default boolean test(@Nonnull T key) {
      return this.get(key).test();
   }

   default boolean testSilently(@Nonnull T key) {
      return this.get(key).testSilently();
   }

   default long elapsed(@Nonnull T key) {
      return this.get(key).elapsed();
   }

   default void reset(@Nonnull T key) {
      this.get(key).reset();
   }

   default long remainingMillis(@Nonnull T key) {
      return this.get(key).remainingMillis();
   }

   default long remainingTime(@Nonnull T key, @Nonnull TimeUnit unit) {
      return this.get(key).remainingTime(unit);
   }

   @Nonnull
   default OptionalLong getLastTested(@Nonnull T key) {
      return this.get(key).getLastTested();
   }

   default void setLastTested(@Nonnull T key, long time) {
      this.get(key).setLastTested(time);
   }
}
