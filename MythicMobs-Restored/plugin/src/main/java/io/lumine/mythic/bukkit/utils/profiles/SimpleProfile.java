package io.lumine.mythic.bukkit.utils.profiles;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class SimpleProfile implements Profile {
   @Nonnull
   private final UUID uniqueId;
   @Nullable
   private final String name;
   private final long timestamp;

   SimpleProfile(@Nonnull UUID uniqueId, @Nullable String name) {
      this.uniqueId = Objects.requireNonNull(uniqueId, "uniqueId");
      this.name = name;
      this.timestamp = System.currentTimeMillis();
   }

   @Nonnull
   @Override
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   @Override
   public Optional<String> getName() {
      return Optional.ofNullable(this.name);
   }

   @Override
   public long getTimestamp() {
      return this.timestamp;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof Profile other) ? false : this.getUniqueId().equals(other.getUniqueId()) && this.getName().equals(other.getName());
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.uniqueId, this.name);
   }

   @Override
   public String toString() {
      return "Profile(uniqueId=" + this.uniqueId + ", name=" + this.name + ")";
   }
}
