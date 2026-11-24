package fr.phoenixdevt.mmoprofiles.shared.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

public class PlayerDataMap {
   private final Map<UUID, ProxiedPlayerData> fromInitialId = new HashMap<>();

   public ProxiedPlayerData fromInitialId(@NotNull UUID initialId) {
      return Objects.requireNonNull(this.fromInitialId.get(initialId), "Could not find player with official ID '" + initialId + "'");
   }

   public void initialize(@NotNull UUID initialId) {
      ProxiedPlayerData old = this.fromInitialId.put(initialId, new ProxiedPlayerData(initialId));
      if (old != null) {
         throw new RuntimeException("Found player with official ID '" + initialId + "'");
      }
   }

   public void unload(@NotNull UUID officialId) {
      Validate.notNull(this.fromInitialId.remove(officialId), "Could not find player with official ID '" + officialId + "'");
   }

   public boolean has(@NotNull UUID initialId) {
      return this.fromInitialId.containsKey(initialId);
   }
}
