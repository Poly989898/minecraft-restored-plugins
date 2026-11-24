package net.Indyuce.mmocore.guild.provided;

import io.lumine.mythic.lib.gui.PluginInventory;
import io.lumine.mythic.lib.version.VInventoryView;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.social.Request;
import net.Indyuce.mmocore.gui.social.guild.EditableGuildView;
import net.Indyuce.mmocore.guild.AbstractGuild;
import net.Indyuce.mmocore.manager.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Guild implements AbstractGuild {
   private final Map<UUID, Long> invites = new HashMap<>();
   private final String guildId;
   private final String guildName;
   private final String guildTag;
   private final Set<UUID> members = new HashSet<>();
   private UUID owner;

   public Guild(UUID owner, String name, String tag) {
      this.owner = owner;
      this.guildId = tag.toLowerCase();
      this.guildName = name;
      this.guildTag = tag;
   }

   public UUID getOwner() {
      return this.owner;
   }

   public String getName() {
      return this.guildName;
   }

   public String getId() {
      return this.guildId;
   }

   public String getTag() {
      return this.guildTag;
   }

   public long getLastInvite(Player player) {
      return this.invites.containsKey(player.getUniqueId()) ? this.invites.get(player.getUniqueId()) : 0L;
   }

   public void removeLastInvite(Player player) {
      this.invites.remove(player.getUniqueId());
   }

   public void removeMember(UUID uuid) {
      this.removeMember(uuid, false);
   }

   public void removeMember(UUID uuid, boolean disband) {
      PlayerData data = PlayerData.get(uuid);
      if (data.isOnline() && VersionUtils.getOpen(data.getPlayer()).getTopInventory().getHolder() instanceof EditableGuildView.GuildViewInventory) {
         InventoryManager.GUILD_CREATION.newInventory(data).open();
      }

      if (!disband) {
         this.members.remove(uuid);
      }

      if (data != null) {
         data.setGuild(null);
      }

      this.reopenInventories();
      if (this.members.size() < 1) {
         MMOCore.plugin.nativeGuildManager.unregisterGuild(this);
      } else {
         if (this.owner.equals(uuid)) {
            this.owner = this.members.stream().findAny().get();
            ConfigMessage.fromKey("transfer-guild-ownership").send(Bukkit.getPlayer(this.owner));
         }
      }
   }

   public void addMember(UUID uuid) {
      PlayerData data = PlayerData.get(uuid);
      if (data.inGuild()) {
         data.getGuild().removeMember(uuid);
      }

      data.setGuild(this);
      this.members.add(uuid);
      this.reopenInventories();
   }

   public void registerMember(UUID uuid) {
      this.members.add(uuid);
   }

   public void reopenInventories() {
      for (UUID uuid : this.members) {
         PlayerData member = PlayerData.get(uuid);
         VInventoryView open = VersionUtils.getOpen(member.getPlayer());
         if (member.isOnline() && open.getTopInventory().getHolder() instanceof EditableGuildView.GuildViewInventory) {
            ((PluginInventory)open.getTopInventory().getHolder()).open();
         }
      }
   }

   public void sendGuildInvite(PlayerData inviter, PlayerData target) {
      this.invites.put(target.getUniqueId(), System.currentTimeMillis());
      Request request = new GuildInvite(this, inviter, target);
      ConfigMessage.fromKey("guild-invite")
         .addPlaceholders("player", inviter.getPlayer().getName(), "uuid", request.getUniqueId().toString())
         .send(target.getPlayer());
      MMOCore.plugin.requestManager.registerRequest(request);
   }

   @Override
   public boolean hasMember(Player player) {
      return this.hasMember(player.getUniqueId());
   }

   public boolean hasMember(UUID player) {
      return this.members.contains(player);
   }

   public List<UUID> listMembers() {
      return new ArrayList<>(this.members);
   }

   public void forEachMember(Consumer<? super UUID> action) {
      this.members.forEach(action);
   }

   public int countOnlineMembers() {
      int online = 0;

      for (UUID member : this.members) {
         if (Bukkit.getOfflinePlayer(member).isOnline()) {
            online++;
         }
      }

      return online;
   }

   public int countMembers() {
      return this.members.size();
   }
}
