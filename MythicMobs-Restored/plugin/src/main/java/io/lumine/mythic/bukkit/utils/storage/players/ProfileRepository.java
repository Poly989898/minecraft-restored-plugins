package io.lumine.mythic.bukkit.utils.storage.players;

import io.lumine.mythic.bukkit.utils.promise.Promise;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public interface ProfileRepository<P extends Profile> {
   P getProfile(@Nonnull Player player);

   @Nonnull
   Optional<P> getProfile(@Nonnull UUID uuid);

   @Nonnull
   Optional<P> getProfile(@Nonnull String str);

   @Nonnull
   Collection<P> getKnownProfiles();

   @Nonnull
   default Map<UUID, P> getProfiles(@Nonnull Iterable<UUID> uniqueIds) {
      Objects.requireNonNull(uniqueIds, "uniqueIds");
      Map<UUID, P> ret = new HashMap<>();

      for (UUID uniqueId : uniqueIds) {
         Optional<P> maybeProfile = this.getProfile(uniqueId);
         if (maybeProfile.isPresent()) {
            ret.put(uniqueId, maybeProfile.get());
         }
      }

      return ret;
   }

   @Nonnull
   default Map<String, P> getProfilesByName(@Nonnull Iterable<String> names) {
      Objects.requireNonNull(names, "names");
      Map<String, P> ret = new HashMap<>();

      for (String name : names) {
         this.getProfile(name).ifPresent(p -> ret.put(name, p));
      }

      return ret;
   }

   @Nonnull
   Promise<Optional<P>> lookupProfile(@Nonnull UUID uuid, int value);

   @Nonnull
   default Promise<Optional<P>> lookupProfile(@Nonnull String name) {
      return this.lookupProfile(name, 0);
   }

   @Nonnull
   Promise<Optional<P>> lookupProfile(@Nonnull String str, int value);

   Collection<P> getAvailableProfiles(UUID uuid);

   Collection<P> getAvailableProfiles(String str);

   void saveAll();
}
