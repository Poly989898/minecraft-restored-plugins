package net.Indyuce.mmocore.party;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.jetbrains.annotations.NotNull;

public class PartyUtils {
   public static void applyStatBonuses(@NotNull PlayerData player, int memberCount) {
      MMOCore.plugin.partyManager.getBonuses().forEach(buff -> buff.multiply(memberCount - 1).register(player.getMMOPlayerData()));
   }

   public static void clearStatBonuses(@NotNull PlayerData player) {
      MMOCore.plugin.partyManager.getBonuses().forEach(buff -> buff.unregister(player.getMMOPlayerData()));
   }
}
