package net.Indyuce.mmocore.party.compat;

import com.civious.obteam.mechanics.Team;
import com.civious.obteam.mechanics.TeamManager;
import com.civious.obteam.mechanics.TeamMember;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.PartyModule;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

public class OBTeamPartyModule implements PartyModule, Listener {
   @Nullable
   @Override
   public AbstractParty getParty(PlayerData playerData) {
      Team team = TeamManager.getInstance().getTeam(playerData.getPlayer());
      return team == null ? null : new OBTeamPartyModule.CustomParty(team);
   }

   private static class CustomParty implements AbstractParty {
      private final Team team;

      public CustomParty(Team team) {
         this.team = team;
      }

      @Override
      public boolean hasMember(Player player) {
         for (TeamMember member : this.team.getMembers()) {
            if (member.getOfflinePlayer().getUniqueId().equals(player.getUniqueId())) {
               return true;
            }
         }

         return false;
      }

      @Override
      public List<PlayerData> getOnlineMembers() {
         List<PlayerData> list = new ArrayList<>();

         for (TeamMember member : this.team.getMembersAndOwner()) {
            try {
               list.add(PlayerData.get(member.getOfflinePlayer()));
            } catch (Exception var5) {
            }
         }

         return list;
      }

      @Override
      public int countMembers() {
         return this.team.getMembersAndOwner().size();
      }
   }
}
