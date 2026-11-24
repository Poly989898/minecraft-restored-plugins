package net.Indyuce.mmocore.manager.data;

import io.lumine.mythic.lib.data.OfflineDataHolder;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;

public interface OfflinePlayerData extends OfflineDataHolder {
   void removeFriend(UUID uuid);

   boolean hasFriend(UUID uuid);

   PlayerClass getProfess();

   int getLevel();

   long getLastLogin();

   static OfflinePlayerData get(UUID uuid) {
      return (OfflinePlayerData)MMOCore.plugin.playerDataManager.getOffline(uuid);
   }
}
