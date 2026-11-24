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
   public SetTagCommandTreeNode(CommandTreeNode var1) {
      super(var1, "settag");
      this.addParameter(new Parameter("<path>", (var0, var1x) -> var1x.add("TagPath")));
      this.addParameter(new Parameter("<value>", (var0, var1x) -> var1x.add("TagValue")));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 4) {
         return CommandResult.THROW_USAGE;
      }

      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      }

      try {
         Player var3 = (Player)var1;
         var3.getInventory()
            .setItemInMainHand(
               MythicLib.plugin
                  .getVersion()
                  .getWrapper()
                  .getNBTItem(var3.getInventory().getItemInMainHand())
                  .addTag(new ItemTag[]{new ItemTag(var2[2].toUpperCase().replace("-", "_"), var2[3].replace("%%", " "))})
                  .toItem()
            );
         var3.sendMessage("Successfully set tag.");
         return CommandResult.SUCCESS;
      } catch (Exception var4) {
         var1.sendMessage("Couldn't set tag.");
         return CommandResult.FAILURE;
      }
   }
}
