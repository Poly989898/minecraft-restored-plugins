package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.util.Random;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TakeCommandTreeNode extends CommandTreeNode {
   private static final Random random = new Random();

   public TakeCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "take");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(Parameter.PLAYER);
      this.addParameter(Parameter.AMOUNT);
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length != 5) {
         return CommandResult.THROW_USAGE;
      }

      try {
         Type type = MMOItems.plugin.getTypes().getOrThrow(texts[1].toUpperCase().replace("-", "_"));
         String text = texts[2].toUpperCase().replace("-", "_");
         Player player = Bukkit.getPlayer(texts[3]);
         Validate.notNull(player, "Could not find player called '" + texts[3] + "'.");
         int count = Integer.parseInt(texts[4]);

         for (int i = 0; i < player.getInventory().getSize() && count > 0; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
               NBTItem nbtItem = NBTItem.get(item);
               String itemType = nbtItem.getType();
               if (type.getId().equals(itemType) && nbtItem.getString("MMOITEMS_ITEM_ID").equals(text)) {
                  int takenAmount = Math.min(count, item.getAmount());
                  count -= takenAmount;
                  item.setAmount(item.getAmount() - takenAmount);
                  player.getInventory().setItem(i, item);
               }
            }
         }

         return CommandResult.SUCCESS;
      } catch (IllegalArgumentException exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + exception.getMessage());
         return CommandResult.FAILURE;
      }
   }
}
