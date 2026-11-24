package net.Indyuce.mmoitems.command.mmoitems.item;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairCommandTreeNode extends CommandTreeNode {
   public RepairCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "repair");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         Player player = (Player)sender;
         ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(itemInMainHand);
         if (!nBTItem.hasTag("MMOITEMS_DURABILITY")) {
            sender.sendMessage(MMOItems.plugin.getPrefix() + "The item you are holding can't be repaired.");
            return CommandResult.FAILURE;
         } else {
            DurabilityItem durabilityItem = new DurabilityItem(player, itemInMainHand);
            player.getInventory().setItemInMainHand(durabilityItem.addDurability(durabilityItem.getMaxDurability()).toItem());
            sender.sendMessage(MMOItems.plugin.getPrefix() + "Successfully repaired the item you are holding.");
            return CommandResult.SUCCESS;
         }
      }
   }
}
