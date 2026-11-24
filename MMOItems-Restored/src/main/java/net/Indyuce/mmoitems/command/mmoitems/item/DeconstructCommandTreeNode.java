package net.Indyuce.mmoitems.command.mmoitems.item;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.util.List;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeconstructCommandTreeNode extends CommandTreeNode {
   public DeconstructCommandTreeNode(CommandTreeNode var1) {
      super(var1, "deconstruct");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      }

      Player var3 = (Player)var1;
      ItemStack var4 = var3.getInventory().getItemInMainHand();
      NBTItem var5 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var4);
      String var6 = var5.getString("MMOITEMS_TIER");
      if (var6.equals("")) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + "The item you are holding has no tier.");
         return CommandResult.FAILURE;
      }

      ItemTier var7 = MMOItems.plugin.getTiers().get(var6);
      if (var7 == null) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + "The item tier no longer exists.");
         return CommandResult.FAILURE;
      }

      PlayerData var8 = PlayerData.get(var3);
      List<ItemStack> var9 = var7.getDeconstructedLoot(var8);
      if (var9.isEmpty()) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + "There we no items to be yielded from the deconstruction.");
         return CommandResult.FAILURE;
      }

      var4.setAmount(var4.getAmount() - 1);
      var3.getInventory().setItemInMainHand(var4);

      for (ItemStack var11 : var3.getInventory().addItem(var9.toArray(new ItemStack[0])).values()) {
         var3.getWorld().dropItem(var3.getLocation(), var11);
      }

      var1.sendMessage(MMOItems.plugin.getPrefix() + "Successfully deconstructed the item you are holding.");
      return CommandResult.SUCCESS;
   }
}
