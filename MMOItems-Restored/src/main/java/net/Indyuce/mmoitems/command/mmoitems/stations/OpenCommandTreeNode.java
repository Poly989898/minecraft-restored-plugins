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
   public OpenCommandTreeNode(CommandTreeNode var1) {
      super(var1, "open");
      this.addParameter(new Parameter("<station>", (var0, var1x) -> MMOItems.plugin.getCrafting().getStations().forEach(var1xx -> var1x.add(var1xx.getId()))));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         return CommandResult.THROW_USAGE;
      } else if (!MMOItems.plugin.getCrafting().hasStation(var2[2])) {
         var1.sendMessage(ChatColor.RED + "There is no station called " + var2[2] + ".");
         return CommandResult.FAILURE;
      } else {
         Player var3 = var2.length > 3 ? Bukkit.getPlayer(var2[3]) : (var1 instanceof Player ? (Player)var1 : null);
         if (var3 == null) {
            var1.sendMessage(ChatColor.RED + "Please specify a valid player.");
            return CommandResult.FAILURE;
         } else {
            new CraftingStationView(var3, MMOItems.plugin.getCrafting().getStation(var2[2]), 1).open();
            return CommandResult.SUCCESS;
         }
      }
   }
}
