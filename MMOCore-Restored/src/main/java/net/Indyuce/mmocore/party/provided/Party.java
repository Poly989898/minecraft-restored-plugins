package net.Indyuce.mmocore.party.provided;

import io.lumine.mythic.lib.gui.PluginInventory;
import io.lumine.mythic.lib.version.VInventoryView;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.social.Request;
import net.Indyuce.mmocore.gui.social.party.EditablePartyView;
import net.Indyuce.mmocore.manager.InventoryManager;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.PartyUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Party implements AbstractParty {
   private final List<PlayerData> members = new ArrayList<>();
   private final Map<UUID, Long> invites = new HashMap<>();
   private final UUID id = UUID.randomUUID();
   @NotNull
   private PlayerData owner;
   private final MMOCorePartyModule module;

   public Party(MMOCorePartyModule module, PlayerData owner) {
      this.module = module;
      this.owner = owner;
      this.addMember(owner);
   }

   public UUID getUniqueId() {
      return this.id;
   }

   public PlayerData getOwner() {
      return this.owner;
   }

   public List<PlayerData> getMembers() {
      return this.members;
   }

   @Override
   public List<PlayerData> getOnlineMembers() {
      List<PlayerData> online = new ArrayList<>();

      for (PlayerData member : this.members) {
         if (member.isOnline()) {
            online.add(member);
         }
      }

      return online;
   }

   public int getLevel() {
      return this.owner.getLevel();
   }

   @Override
   public int countMembers() {
      return this.members.size();
   }

   @Override
   public PlayerData getMember(int index) {
      return this.members.get(index);
   }

   public long getLastInvite(Player player) {
      return this.invites.containsKey(player.getUniqueId()) ? this.invites.get(player.getUniqueId()) : 0L;
   }

   public void removeLastInvite(Player player) {
      this.invites.remove(player.getUniqueId());
   }

   @Override
   public boolean hasMember(Player player) {
      return this.hasMember(player.getUniqueId());
   }

   public boolean hasMember(UUID uuid) {
      for (PlayerData member : this.members) {
         if (member.getUniqueId().equals(uuid)) {
            return true;
         }
      }

      return false;
   }

   public void removeMember(PlayerData data) {
      this.removeMember(data, true);
   }

   public void removeMember(PlayerData data, boolean notify) {
      if (data.isOnline() && VersionUtils.getOpen(data.getPlayer()).getTopInventory().getHolder() instanceof EditablePartyView.PartyViewInventory) {
         InventoryManager.PARTY_CREATION.newInventory(data).open();
      }

      this.members.remove(data);
      this.module.setParty(data, null);
      PartyUtils.clearStatBonuses(data);
      this.members.forEach(member -> PartyUtils.applyStatBonuses(member, this.members.size()));
      this.updateOpenInventories();
      if (this.members.size() < 1) {
         this.module.unregisterParty(this);
      } else {
         if (this.owner.equals(data)) {
            this.owner = this.members.get(0);
            if (notify && this.owner.isOnline()) {
               ConfigMessage.fromKey("transfer-party-ownership").send(this.owner.getPlayer());
            }
         }
      }
   }

   public void addMember(PlayerData data) {
      Party party = (Party)data.getParty();
      if (party != null) {
         party.removeMember(data);
      }

      this.module.setParty(data, this);
      this.members.add(data);
      this.members.forEach(member -> PartyUtils.applyStatBonuses(member, this.members.size()));
      this.updateOpenInventories();
   }

   private void updateOpenInventories() {
      for (PlayerData member : this.members) {
         VInventoryView open = VersionUtils.getOpen(member.getPlayer());
         if (member.isOnline() && open.getTopInventory().getHolder() instanceof EditablePartyView.PartyViewInventory) {
            ((PluginInventory)open.getTopInventory().getHolder()).open();
         }
      }
   }

   @Deprecated
   public void sendPartyInvite(PlayerData inviter, PlayerData target) {
      this.sendInvite(inviter, target);
   }

   public void sendInvite(PlayerData inviter, PlayerData target) {
      this.invites.put(target.getUniqueId(), System.currentTimeMillis());
      Request request = new PartyInvite(this, inviter, target);
      ConfigMessage.fromKey("party-invite")
         .addPlaceholders("player", inviter.getPlayer().getName(), "uuid", request.getUniqueId().toString())
         .send(target.getPlayer());
      MMOCore.plugin.requestManager.registerRequest(request);
   }

   public void forEachMember(Consumer<PlayerData> action) {
      new ArrayList<>(this.members).forEach(action);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Party party = (Party)o;
         return this.id.equals(party.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}
