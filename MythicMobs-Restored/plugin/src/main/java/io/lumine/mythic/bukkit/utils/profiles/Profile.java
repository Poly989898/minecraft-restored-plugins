package io.lumine.mythic.bukkit.utils.profiles;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.entity.HumanEntity;

public interface Profile {
   @Nonnull
   static Profile create(@Nonnull UUID uniqueId, @Nullable String name) {
      return new SimpleProfile(uniqueId, name);
   }

   @Nonnull
   static Profile create(HumanEntity player) {
      return new SimpleProfile(player.getUniqueId(), player.getName());
   }

   @Nonnull
   UUID getUniqueId();

   @Nonnull
   Optional<String> getName();

   long getTimestamp();
}
