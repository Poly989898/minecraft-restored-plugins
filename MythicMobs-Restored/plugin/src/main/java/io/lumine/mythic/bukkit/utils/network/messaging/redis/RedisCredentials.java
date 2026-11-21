package io.lumine.mythic.bukkit.utils.network.messaging.redis;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.configuration.ConfigurationSection;

public final class RedisCredentials {
   private final String address;
   private final int port;
   private final String password;

   public static RedisCredentials of(@Nonnull String address, int port, @Nullable String password) {
      return new RedisCredentials(address, port, password);
   }

   public static RedisCredentials of(@Nonnull String address, int port) {
      return new RedisCredentials(address, port, null);
   }

   public static RedisCredentials fromConfig(@Nonnull ConfigurationSection config) {
      return of(config.getString("address", "localhost"), config.getInt("port", 6379), config.getString("password"));
   }

   private RedisCredentials(@Nonnull String address, int port, @Nullable String password) {
      this.address = Objects.requireNonNull(address);
      this.port = port;
      this.password = password == null ? "" : password;
   }

   @Nonnull
   public String getAddress() {
      return this.address;
   }

   public int getPort() {
      return this.port;
   }

   @Nonnull
   public String getPassword() {
      return this.password;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof RedisCredentials other)
            ? false
            : this.getAddress().equals(other.getAddress()) && this.getPort() == other.getPort() && this.getPassword().equals(other.getPassword());
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getAddress().hashCode();
      result = result * 59 + this.getPort();
      return result * 59 + this.getPassword().hashCode();
   }

   @Override
   public String toString() {
      return "RedisCredentials(address=" + this.getAddress() + ", port=" + this.getPort() + ", password=" + this.getPassword() + ")";
   }
}
