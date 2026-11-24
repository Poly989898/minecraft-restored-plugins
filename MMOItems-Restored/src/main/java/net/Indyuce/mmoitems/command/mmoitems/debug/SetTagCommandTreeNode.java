package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTagCommandTreeNode extends CommandTreeNode {
   public SetTagCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "settag");
      this.addParameter(new Parameter("<path>", (commandTreeExplorer, texts) -> texts.add("TagPath")));
      this.addParameter(new Parameter("<value>", (commandTreeExplorer, texts) -> texts.add("TagValue")));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 4) {
         return CommandResult.THROW_USAGE;
      }

      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      }

      try {
         Player player = (Player)sender;
         player.getInventory()
            .setItemInMainHand(
               MythicLib.plugin
                  .getVersion()
                  .getWrapper()
                  .getNBTItem(player.getInventory().getItemInMainHand())
                  .addTag(new ItemTag[]{new ItemTag(texts[2].toUpperCase().replace("-", "_"), texts[3].replace("%%", " "))})
                  .toItem()
            );
         player.sendMessage("Successfully set tag.");
         return CommandResult.SUCCESS;
      } catch (Exception exception) {
         sender.sendMessage("Couldn't set tag.");
         return CommandResult.FAILURE;
      }
   }
}
