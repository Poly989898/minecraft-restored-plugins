package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ItemListCommandTreeNode extends CommandTreeNode {
   public ItemListCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "itemlist");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 2) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(texts[1])) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + texts[1].toUpperCase().replace("-", "_"));
         sender.sendMessage(MMOItems.plugin.getPrefix() + "Type " + ChatColor.GREEN + "/mi list type " + ChatColor.GRAY + "to see all the available item types.");
         return CommandResult.FAILURE;
      }

      Type type = Type.get(texts[1]);
      sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
      sender.sendMessage(ChatColor.GREEN + "List of all items in " + type.getId().toLowerCase() + ".yml:");
      FileConfiguration config = type.getConfigFile().getConfig();
      if (sender instanceof Player) {
         for (String key : config.getKeys(false)) {
            String text = config.getConfigurationSection(key).contains("name")
               ? " " + ChatColor.WHITE + "(" + MythicLib.plugin.parseColors(config.getString(key + ".name")) + ChatColor.WHITE + ")"
               : "";
            MythicLib.plugin
               .getVersion()
               .getWrapper()
               .sendJson(
                  (Player)sender,
                  "{\"text\":\"* "
                     + ChatColor.GREEN
                     + key
                     + text
                     + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/mi edit "
                     + type.getId()
                     + " "
                     + key
                     + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Click to edit "
                     + (text.equals("") ? key : MythicLib.plugin.parseColors(config.getString(key + ".name")))
                     + ChatColor.WHITE
                     + ".\",\"color\":\"white\"}}}"
               );
         }
      } else {
         for (String key : config.getKeys(false)) {
            sender.sendMessage(
               "* "
                  + ChatColor.GREEN
                  + key
                  + (
                     config.getConfigurationSection(key).contains("name")
                        ? " " + ChatColor.WHITE + "(" + MythicLib.plugin.parseColors(config.getString(key + ".name")) + ChatColor.WHITE + ")"
                        : ""
                  )
            );
         }
      }

      return CommandResult.SUCCESS;
   }
}
