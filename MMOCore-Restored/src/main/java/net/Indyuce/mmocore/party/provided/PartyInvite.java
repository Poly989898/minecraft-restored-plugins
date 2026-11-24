package net.Indyuce.mmocore.party.provided;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.social.Request;
import net.Indyuce.mmocore.manager.InventoryManager;

public class PartyInvite extends Request {
   private final Party party;

   public PartyInvite(Party party, PlayerData creator, PlayerData target) {
      super(creator, target);
      this.party = party;
   }

   public Party getParty() {
      return this.party;
   }

   @Override
   public void whenDenied() {
   }

   @Override
   public void whenAccepted() {
      if (this.party.getMembers().size() >= MMOCore.plugin.configManager.maxPartyPlayers) {
         ConfigMessage.fromKey("party-is-full").send(this.getTarget().getPlayer());
      } else {
         if (this.getCreator().isOnline()) {
            this.party.removeLastInvite(this.getCreator().getPlayer());
         }

         this.party.getMembers().forEach(member -> {
            if (member.isOnline()) {
               ConfigMessage.fromKey("party-joined-other", "player", this.getTarget().getPlayer().getName()).send(member.getPlayer());
            }
         });
         if (this.party.getOwner().isOnline()) {
            ConfigMessage.fromKey("party-joined", "owner", this.party.getOwner().getPlayer().getName()).send(this.getTarget().getPlayer());
         }

         this.party.addMember(this.getTarget());
         InventoryManager.PARTY_VIEW.newInventory(this.getTarget()).open();
      }
   }
}
