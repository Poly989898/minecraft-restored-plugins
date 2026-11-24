package fr.phoenixdevt.mmoprofiles.bukkit.command;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommandNode extends CommandTreeNode {
   public ReloadCommandNode(CommandTreeNode parent) {
      super(parent, "reload");
   }

   public CommandResult execute(CommandSender sender, String[] strings) {
      MMOProfiles.plugin.reloadConfig();
      MMOProfiles.plugin.configManager.reload();
      sender.sendMessage(ChatColor.GREEN + MMOProfiles.plugin.getName() + " " + MMOProfiles.plugin.getDescription().getVersion() + " successfully reloaded.");
      return CommandResult.SUCCESS;
   }
}
