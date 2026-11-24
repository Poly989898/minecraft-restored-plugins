package fr.phoenixdevt.mmoprofiles.bukkit.command;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.listener.PlayerListener;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenCommandNode extends CommandTreeNode {
   public OpenCommandNode(CommandTreeNode parent) {
      super(parent, "open");
      this.addParameter(Parameter.PLAYER);
   }

   public CommandResult execute(CommandSender sender, String[] strings) {
      if (strings.length < 2) {
         return CommandResult.THROW_USAGE;
      } else {
         Player target = Bukkit.getPlayer(strings[1]);
         if (target == null) {
            sender.sendMessage(ChatColor.RED + "Could not find player called '" + strings[0] + "'");
            return CommandResult.FAILURE;
         } else {
            PlayerListener.openProfileSelection(MMOProfiles.plugin.getPlayerData(target.getUniqueId()));
            return CommandResult.SUCCESS;
         }
      }
   }
}
