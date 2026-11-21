package io.lumine.mythic.bukkit.utils.network.messaging.util;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public final class ChannelReceiver<T> {
   private T value;
   private long timestamp = 0L;
   private final long expiryMillis;

   public ChannelReceiver(long expiryDuration, @Nonnull TimeUnit unit) {
      this.expiryMillis = unit.toMillis(expiryDuration);
   }

   public void set(T value) {
      this.value = value;
      this.timestamp = System.currentTimeMillis();
   }

   public Optional<T> getLastKnownValue() {
      return Optional.ofNullable(this.value);
   }

   public Optional<T> getValue() {
      long now = System.currentTimeMillis();
      long diff = now - this.timestamp;
      return diff > this.expiryMillis ? Optional.empty() : this.getLastKnownValue();
   }

   public OptionalLong getLastReceivedTimestamp() {
      return this.timestamp == 0L ? OptionalLong.empty() : OptionalLong.of(this.timestamp);
   }

   @Override
   public String toString() {
      return "ChannelReceiver{value=" + this.value + ", timestamp=" + this.timestamp + ", expiryMillis=" + this.expiryMillis + "}";
   }
}
