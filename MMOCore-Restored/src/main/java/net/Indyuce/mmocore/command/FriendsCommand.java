package net.Indyuce.mmocore.command;

import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.event.MMOCommandEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.social.FriendRequest;
import net.Indyuce.mmocore.api.player.social.Request;
import net.Indyuce.mmocore.command.api.RegisteredCommand;
import net.Indyuce.mmocore.command.api.ToggleableCommand;
import net.Indyuce.mmocore.manager.InventoryManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class FriendsCommand extends RegisteredCommand {
   public FriendsCommand(ConfigurationSection config) {
      super(config, ToggleableCommand.FRIENDS);
   }

   public boolean execute(CommandSender sender, String label, String[] args) {
      if (!sender.hasPermission("mmocore.friends")) {
         return false;
      }

      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is for players only.");
         return true;
      }

      PlayerData data = PlayerData.get((Player)sender);
      MMOCommandEvent event = new MMOCommandEvent(data, "friends");
      Bukkit.getServer().getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         return true;
      }

      if (args.length <= 1) {
         InventoryManager.FRIEND_LIST.newInventory(data).open();
         return true;
      }

      FriendRequest invite;
      if (args.length > 1) {
         try {
            UUID uuid = UUID.fromString(args[1]);
            Request req = MMOCore.plugin.requestManager.getRequest(uuid);
            Validate.isTrue(!req.isTimedOut() && req instanceof FriendRequest);
            Validate.isTrue(!data.hasFriend(req.getCreator().getUniqueId()));
            invite = (FriendRequest)req;
         } catch (Exception exception) {
            return true;
         }
      } else {
         invite = MMOCore.plugin.requestManager.findRequest(data, FriendRequest.class);
      }

      if (invite == null) {
         return true;
      }

      if (args[0].equalsIgnoreCase("accept")) {
         invite.accept();
      }

      if (args[0].equalsIgnoreCase("deny")) {
         invite.deny();
      }

      return true;
   }
}
