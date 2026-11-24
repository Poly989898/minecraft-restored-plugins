package net.Indyuce.mmocore.party.compat;

import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.provided.MMOCorePartyModule;
import net.Indyuce.mmocore.party.provided.Party;
import net.playavalon.mythicdungeons.api.party.IDungeonParty;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MythicDungeonsPartyInjector extends MMOCorePartyModule {
   public MythicDungeonsPartyInjector() {
      MMOCore.plugin.getLogger().log(Level.INFO, "Injecting MMOCore party system into MythicDungeons");
   }

   @Override
   public Party newRegisteredParty(PlayerData owner) {
      MythicDungeonsPartyInjector.CustomParty party = new MythicDungeonsPartyInjector.CustomParty(this, owner);
      this.parties.add(party);
      party.initDungeonParty(MMOCore.plugin);
      return party;
   }

   private static class CustomParty extends Party implements IDungeonParty {
      public CustomParty(MMOCorePartyModule module, PlayerData owner) {
         super(module, owner);
      }

      public void addPlayer(Player player) {
         this.addMember(PlayerData.get(player));
      }

      public void removePlayer(Player player) {
         this.removeMember(PlayerData.get(player));
      }

      public List<Player> getPlayers() {
         return this.getMembers().stream().<Player>map(SynchronizedDataHolder::getPlayer).collect(Collectors.toList());
      }

      @NotNull
      public OfflinePlayer getLeader() {
         return this.getOwner().getPlayer();
      }
   }
}
