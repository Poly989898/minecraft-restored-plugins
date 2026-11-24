package net.Indyuce.mmoitems.command.mmoitems.item;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnidentifyCommandTreeNode extends CommandTreeNode {
   public UnidentifyCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "unidentify");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         Player player = (Player)sender;
         NBTItem nbtItem = NBTItem.get(player.getInventory().getItemInMainHand());
         if (nbtItem.getType() == null) {
            sender.sendMessage(MMOItems.plugin.getPrefix() + "Couldn't unidentify the item you are holding.");
            return CommandResult.FAILURE;
         } else if (nbtItem.getBoolean("MMOITEMS_UNIDENTIFIED")) {
            sender.sendMessage(MMOItems.plugin.getPrefix() + "The item you are holding is already unidentified.");
            return CommandResult.FAILURE;
         } else {
            player.getInventory().setItemInMainHand(Type.get(nbtItem.getType()).getUnidentifiedTemplate().newBuilder(nbtItem).build());
            sender.sendMessage(MMOItems.plugin.getPrefix() + "Successfully unidentified the item you are holding.");
            return CommandResult.SUCCESS;
         }
      }
   }
}
