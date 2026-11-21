package io.lumine.mythic.bukkit.utils.profiles;

import io.lumine.mythic.bukkit.utils.promise.Promise;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface ProfileRepository {
   @Nonnull
   Profile getProfile(@Nonnull UUID uuid);

   @Nonnull
   Optional<Profile> getProfile(@Nonnull String str);

   @Nonnull
   Collection<Profile> getKnownProfiles();

   @Nonnull
   default Map<UUID, Profile> getProfiles(@Nonnull Iterable<UUID> uniqueIds) {
      Objects.requireNonNull(uniqueIds, "uniqueIds");
      Map<UUID, Profile> ret = new HashMap<>();

      for (UUID uniqueId : uniqueIds) {
         Profile profile = this.getProfile(uniqueId);
         if (profile.getName().isPresent()) {
            ret.put(uniqueId, profile);
         }
      }

      return ret;
   }

   @Nonnull
   default Map<String, Profile> getProfilesByName(@Nonnull Iterable<String> names) {
      Objects.requireNonNull(names, "names");
      Map<String, Profile> ret = new HashMap<>();

      for (String name : names) {
         this.getProfile(name).ifPresent(p -> ret.put(name, p));
      }

      return ret;
   }

   @Nonnull
   Promise<Profile> lookupProfile(@Nonnull UUID uuid);

   @Nonnull
   Promise<Optional<Profile>> lookupProfile(@Nonnull String str);

   @Nonnull
   Promise<Collection<Profile>> lookupKnownProfiles();

   @Nonnull
   Promise<Map<UUID, Profile>> lookupProfiles(@Nonnull Iterable<UUID> uuids);

   @Nonnull
   Promise<Map<String, Profile>> lookupProfilesByName(@Nonnull Iterable<String> strs);
}
