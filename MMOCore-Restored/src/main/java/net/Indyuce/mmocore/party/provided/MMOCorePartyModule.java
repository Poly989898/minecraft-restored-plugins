package net.Indyuce.mmocore.party.provided;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.event.social.PartyChatEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.PartyModule;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MMOCorePartyModule implements PartyModule, Listener {
   protected final Set<Party> parties = new HashSet<>();
   protected final Map<UUID, Party> playerParties = new HashMap<>();

   public MMOCorePartyModule() {
      Bukkit.getPluginManager().registerEvents(this, MMOCore.plugin);
   }

   @Deprecated
   public void registerParty(Party party) {
      this.parties.add(party);
   }

   public Party newRegisteredParty(PlayerData owner) {
      Party party = new Party(this, owner);
      this.parties.add(party);
      return party;
   }

   public boolean isRegistered(Party party) {
      return this.parties.contains(party);
   }

   public void unregisterParty(Party party) {
      party.forEachMember(party::removeMember);
      Validate.isTrue(party.getMembers().isEmpty(), "Tried unregistering a non-empty party");
      this.parties.remove(party);
   }

   public Party getParty(PlayerData playerData) {
      return this.playerParties.get(playerData.getUniqueId());
   }

   public void setParty(PlayerData playerData, Party party) {
      this.playerParties.put(playerData.getUniqueId(), party);
   }

   @EventHandler(priority = EventPriority.LOW)
   public void partyChat(AsyncPlayerChatEvent event) {
      if (event.getMessage().startsWith(MMOCore.plugin.configManager.partyChatPrefix)) {
         PlayerData data = PlayerData.get(event.getPlayer());
         Party party = this.getParty(data);
         if (party != null) {
            event.setCancelled(true);
            Bukkit.getScheduler()
               .runTask(
                  MMOCore.plugin,
                  () -> {
                     ConfigMessage message = ConfigMessage.fromKey(
                        "party-chat",
                        "player",
                        data.getPlayer().getName(),
                        "message",
                        event.getMessage().substring(MMOCore.plugin.configManager.partyChatPrefix.length())
                     );
                     PartyChatEvent called = new PartyChatEvent(party, data, message.asLine());
                     Bukkit.getPluginManager().callEvent(called);
                     if (!called.isCancelled()) {
                        party.getOnlineMembers().forEach(member -> message.send(member.getPlayer()));
                     }
                  }
               );
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void leavePartyOnQuit(PlayerQuitEvent event) {
      PlayerData playerData = PlayerData.get(event.getPlayer());
      AbstractParty party = playerData.getParty();
      if (party != null) {
         ((Party)party).removeMember(playerData);
      }
   }
}
