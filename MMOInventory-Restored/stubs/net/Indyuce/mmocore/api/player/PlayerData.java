package net.Indyuce.mmocore.api.player;

import java.util.UUID;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;

public class PlayerData {
   public static PlayerData get(UUID uuid) { throw new UnsupportedOperationException(); }
   public int getLevel() { return 0; }
   public PlayerClass getProfess() { throw new UnsupportedOperationException(); }
}
