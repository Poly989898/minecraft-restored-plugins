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
   public DeconstructCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "deconstruct");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      }

      Player player = (Player)sender;
      ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
      NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(itemInMainHand);
      String mMOITEMSTIER = nBTItem.getString("MMOITEMS_TIER");
      if (mMOITEMSTIER.equals("")) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + "The item you are holding has no tier.");
         return CommandResult.FAILURE;
      }

      ItemTier itemTier = MMOItems.plugin.getTiers().get(mMOITEMSTIER);
      if (itemTier == null) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + "The item tier no longer exists.");
         return CommandResult.FAILURE;
      }

      PlayerData playerData = PlayerData.get(player);
      List<ItemStack> deconstructedLoot = itemTier.getDeconstructedLoot(playerData);
      if (deconstructedLoot.isEmpty()) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + "There we no items to be yielded from the deconstruction.");
         return CommandResult.FAILURE;
      }

      itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
      player.getInventory().setItemInMainHand(itemInMainHand);

      for (ItemStack item : player.getInventory().addItem(deconstructedLoot.toArray(new ItemStack[0])).values()) {
         player.getWorld().dropItem(player.getLocation(), item);
      }

      sender.sendMessage(MMOItems.plugin.getPrefix() + "Successfully deconstructed the item you are holding.");
      return CommandResult.SUCCESS;
   }
}
