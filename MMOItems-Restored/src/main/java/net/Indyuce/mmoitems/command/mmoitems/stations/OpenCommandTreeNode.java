package net.Indyuce.mmoitems.command.mmoitems.stations;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.gui.CraftingStationView;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenCommandTreeNode extends CommandTreeNode {
   public OpenCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "open");
      this.addParameter(new Parameter("<station>", (commandTreeExplorer, var1x) -> MMOItems.plugin.getCrafting().getStations().forEach(var1xx -> var1x.add(var1xx.getId()))));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 3) {
         return CommandResult.THROW_USAGE;
      } else if (!MMOItems.plugin.getCrafting().hasStation(texts[2])) {
         sender.sendMessage(ChatColor.RED + "There is no station called " + texts[2] + ".");
         return CommandResult.FAILURE;
      } else {
         Player player = texts.length > 3 ? Bukkit.getPlayer(texts[3]) : (sender instanceof Player ? (Player)sender : null);
         if (player == null) {
            sender.sendMessage(ChatColor.RED + "Please specify a valid player.");
            return CommandResult.FAILURE;
         } else {
            new CraftingStationView(player, MMOItems.plugin.getCrafting().getStation(texts[2]), 1).open();
            return CommandResult.SUCCESS;
         }
      }
   }
}
