package io.lumine.mythic.core.players.factions;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import java.util.Optional;
import java.util.UUID;

public interface FactionProvider {
   boolean isInFaction(AbstractPlayer abstractPlayer, String str);

   default Optional<String> getFaction(AbstractPlayer player) {
      return this.getFaction(player.getUniqueId());
   }

   Optional<String> getFaction(UUID uuid);
}
