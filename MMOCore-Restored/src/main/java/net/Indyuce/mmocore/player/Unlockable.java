package net.Indyuce.mmocore.player;

import net.Indyuce.mmocore.api.player.PlayerData;

public interface Unlockable {
   String getUnlockNamespacedKey();

   boolean isUnlockedByDefault();

   void whenLocked(PlayerData var1);

   void whenUnlocked(PlayerData var1);
}
