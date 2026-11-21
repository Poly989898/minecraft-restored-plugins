package io.lumine.mythic.bukkit.utils.storage.players;

import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.storage.StorageAdapter;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PlayerStorageAdapter<P extends Profile> extends StorageAdapter<P> {
   default Promise<Optional<P>> load(UUID key) {
      return this.load(key, 0);
   }

   Promise<Optional<P>> load(UUID uuid, int value);

   Promise<Boolean> save(UUID uuid, P p);

   boolean saveSync(UUID uuid, P p);

   default Promise<Optional<P>> loadByName(String key) {
      return this.loadByName(key, 0);
   }

   Promise<Optional<P>> loadByName(String str, int value);

   default Collection<P> getAvailableProfiles(UUID uniqueId) {
      throw new IllegalStateException("PlayerRepository must implement MultiProfile to look up profiles");
   }

   default Collection<P> getAvailableProfiles(String name) {
      throw new IllegalStateException("PlayerRepository must implement MultiProfile to look up profiles");
   }
}
