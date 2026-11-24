package net.Indyuce.mmocore.guild.provided;

import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.social.Request;
import net.Indyuce.mmocore.manager.InventoryManager;
import org.bukkit.Bukkit;

public class GuildInvite extends Request {
   private final Guild guild;

   public GuildInvite(Guild guild, PlayerData creator, PlayerData target) {
      super(creator, target);
      this.guild = guild;
   }

   public Guild getGuild() {
      return this.guild;
   }

   @Override
   public void whenDenied() {
   }

   @Override
   public void whenAccepted() {
      this.guild.removeLastInvite(this.getCreator().getPlayer());
      this.guild.forEachMember(member -> {
         if (Bukkit.getPlayer(member) != null) {
            ConfigMessage.fromKey("guild-joined-other", "player", this.getTarget().getPlayer().getName()).send(Bukkit.getPlayer(member));
            ConfigMessage.fromKey("guild-joined", "owner", Bukkit.getPlayer(this.guild.getOwner()).getName()).send(this.getTarget().getPlayer());
         }
      });
      this.guild.addMember(this.getTarget().getUniqueId());
      InventoryManager.GUILD_VIEW.newInventory(this.getTarget()).open();
   }
}
