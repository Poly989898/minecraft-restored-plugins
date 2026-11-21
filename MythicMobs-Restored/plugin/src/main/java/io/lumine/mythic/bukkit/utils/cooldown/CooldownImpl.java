package io.lumine.mythic.bukkit.utils.cooldown;

import com.google.gson.JsonElement;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

@NonnullByDefault
class CooldownImpl implements Cooldown {
   private long lastTested;
   private final long timeout;

   CooldownImpl(long amount, TimeUnit unit) {
      this.timeout = unit.toMillis(amount);
      this.lastTested = 0L;
   }

   @Override
   public OptionalLong getLastTested() {
      return this.lastTested == 0L ? OptionalLong.empty() : OptionalLong.of(this.lastTested);
   }

   @Override
   public void setLastTested(long time) {
      if (time <= 0L) {
         this.lastTested = 0L;
      } else {
         this.lastTested = time;
      }
   }

   @Override
   public long getTimeout() {
      return this.timeout;
   }

   public CooldownImpl copy() {
      return new CooldownImpl(this.timeout, TimeUnit.MILLISECONDS);
   }

   @Nonnull
   @Override
   public JsonElement serialize() {
      return JsonBuilder.object().add("lastTested", this.lastTested).add("timeout", this.timeout).build();
   }
}
