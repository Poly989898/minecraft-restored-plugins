package io.lumine.mythic.core.players;

import java.util.UUID;

public abstract class PlayerLevelProvider {
   public abstract long getPlayerLevel(UUID uuid);

   public abstract long getPlayerLevel(UUID uuid, String str);
}
