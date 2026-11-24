package net.Indyuce.mmocore.party.compat;

import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.PartyModule;
import net.playavalon.mythicdungeons.api.MythicDungeonsService;
import net.playavalon.mythicdungeons.player.party.partysystem.MythicParty;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

public class MythicDungeonsPartyModule implements PartyModule, Listener {
   private final MythicDungeonsService hook = (MythicDungeonsService)Bukkit.getServer().getServicesManager().load(MythicDungeonsService.class);

   public MythicDungeonsPartyModule() {
      Validate.notNull(this.hook, "Could not load compatibility with MythicDungeons");
   }

   @Nullable
   @Override
   public AbstractParty getParty(PlayerData playerData) {
      MythicParty party = this.hook.getParty(playerData.getPlayer());
      return party == null ? null : new MythicDungeonsPartyModule.CustomParty(party);
   }

   private static class CustomParty implements AbstractParty {
      private final MythicParty party;

      public CustomParty(MythicParty party) {
         this.party = party;
      }

      @Override
      public boolean hasMember(Player player) {
         for (Player member : this.party.getPlayers()) {
            if (member.getUniqueId().equals(player.getUniqueId())) {
               return true;
            }
         }

         return false;
      }

      @Override
      public List<PlayerData> getOnlineMembers() {
         List<PlayerData> list = new ArrayList<>();

         for (Player member : this.party.getPlayers()) {
            try {
               list.add(PlayerData.get(member.getPlayer()));
            } catch (Exception var5) {
            }
         }

         return list;
      }

      @Override
      public int countMembers() {
         return this.party.getPlayers().size();
      }
   }
}
