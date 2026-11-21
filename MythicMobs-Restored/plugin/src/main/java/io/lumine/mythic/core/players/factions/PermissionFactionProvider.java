package io.lumine.mythic.core.players.factions;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import java.util.Optional;
import java.util.UUID;

public class PermissionFactionProvider implements FactionProvider {
   @Override
   public boolean isInFaction(AbstractPlayer player, String faction) {
      return player.hasPermission("faction." + faction.toLowerCase()) && !player.hasPermission("mythicmobs.nofaction");
   }

   @Override
   public Optional<String> getFaction(UUID player) {
      return Optional.empty();
   }
}
