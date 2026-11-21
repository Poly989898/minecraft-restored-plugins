package io.lumine.mythic.bukkit.utils.cooldown;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.tasks.Ticks;
import io.lumine.mythic.bukkit.utils.time.Time;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public interface Cooldown extends GsonSerializable {
   static Cooldown deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("lastTested"));
      Preconditions.checkArgument(object.has("timeout"));
      long lastTested = object.get("lastTested").getAsLong();
      long timeout = object.get("timeout").getAsLong();
      Cooldown c = of(timeout, TimeUnit.MILLISECONDS);
      c.setLastTested(lastTested);
      return c;
   }

   @Nonnull
   static Cooldown ofTicks(long ticks) {
      return new CooldownImpl(Ticks.to(ticks, TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
   }

   @Nonnull
   static Cooldown of(long amount, @Nonnull TimeUnit unit) {
      return new CooldownImpl(amount, unit);
   }

   default boolean test() {
      if (!this.testSilently()) {
         return false;
      } else {
         this.reset();
         return true;
      }
   }

   default boolean testSilently() {
      return this.elapsed() > this.getTimeout();
   }

   default long elapsed() {
      return Time.nowMillis() - this.getLastTested().orElse(0L);
   }

   default void reset() {
      this.setLastTested(Time.nowMillis());
   }

   default long remainingMillis() {
      long diff = this.elapsed();
      return diff > this.getTimeout() ? 0L : this.getTimeout() - diff;
   }

   default long remainingTime(TimeUnit unit) {
      return Math.max(0L, unit.convert(this.remainingMillis(), TimeUnit.MILLISECONDS));
   }

   @Nonnull
   OptionalLong getLastTested();

   void setLastTested(long value);

   long getTimeout();

   @Nonnull
   Cooldown copy();
}
