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
   public UnidentifyCommandTreeNode(CommandTreeNode var1) {
      super(var1, "unidentify");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         Player var3 = (Player)var1;
         NBTItem var4 = NBTItem.get(var3.getInventory().getItemInMainHand());
         if (var4.getType() == null) {
            var1.sendMessage(MMOItems.plugin.getPrefix() + "Couldn't unidentify the item you are holding.");
            return CommandResult.FAILURE;
         } else if (var4.getBoolean("MMOITEMS_UNIDENTIFIED")) {
            var1.sendMessage(MMOItems.plugin.getPrefix() + "The item you are holding is already unidentified.");
            return CommandResult.FAILURE;
         } else {
            var3.getInventory().setItemInMainHand(Type.get(var4.getType()).getUnidentifiedTemplate().newBuilder(var4).build());
            var1.sendMessage(MMOItems.plugin.getPrefix() + "Successfully unidentified the item you are holding.");
            return CommandResult.SUCCESS;
         }
      }
   }
}
