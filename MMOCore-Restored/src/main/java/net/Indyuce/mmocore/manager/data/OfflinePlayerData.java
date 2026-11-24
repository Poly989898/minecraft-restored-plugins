package net.Indyuce.mmocore.manager.data;

import io.lumine.mythic.lib.data.OfflineDataHolder;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;

public interface OfflinePlayerData extends OfflineDataHolder {
   void removeFriend(UUID var1);

   boolean hasFriend(UUID var1);

   PlayerClass getProfess();

   int getLevel();

   long getLastLogin();

   static OfflinePlayerData get(UUID uuid) {
      return (OfflinePlayerData)MMOCore.plugin.playerDataManager.getOffline(uuid);
   }
}
