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
   public RepairCommandTreeNode(CommandTreeNode var1) {
      super(var1, "repair");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         Player var3 = (Player)var1;
         ItemStack var4 = var3.getInventory().getItemInMainHand();
         NBTItem var5 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var4);
         if (!var5.hasTag("MMOITEMS_DURABILITY")) {
            var1.sendMessage(MMOItems.plugin.getPrefix() + "The item you are holding can't be repaired.");
            return CommandResult.FAILURE;
         } else {
            DurabilityItem var6 = new DurabilityItem(var3, var4);
            var3.getInventory().setItemInMainHand(var6.addDurability(var6.getMaxDurability()).toItem());
            var1.sendMessage(MMOItems.plugin.getPrefix() + "Successfully repaired the item you are holding.");
            return CommandResult.SUCCESS;
         }
      }
   }
}
