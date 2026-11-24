package net.Indyuce.mmocore.api.player;

import java.util.UUID;

public class PlayerData {
   public static PlayerData get(UUID uuid) { throw new UnsupportedOperationException(); }
   public int getLevel() { return 0; }
   public PlayerData.PlayerClass getProfess() { throw new UnsupportedOperationException(); }
   public static class PlayerClass { public String getName() { return null; } }
}
