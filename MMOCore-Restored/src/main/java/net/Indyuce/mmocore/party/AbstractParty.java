package net.Indyuce.mmocore.party;

import java.util.List;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface AbstractParty {
   default boolean hasMember(@NotNull Player player) {
      for (PlayerData member : this.getOnlineMembers()) {
         if (member.getPlayer().equals(player)) {
            return true;
         }
      }

      return false;
   }

   List<PlayerData> getOnlineMembers();

   default PlayerData getMember(int n) {
      return this.getOnlineMembers().get(n);
   }

   int countMembers();
}
