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

   public TakeCommandTreeNode(CommandTreeNode var1) {
      super(var1, "take");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(Parameter.PLAYER);
      this.addParameter(Parameter.AMOUNT);
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length != 5) {
         return CommandResult.THROW_USAGE;
      }

      try {
         Type var3 = MMOItems.plugin.getTypes().getOrThrow(var2[1].toUpperCase().replace("-", "_"));
         String var4 = var2[2].toUpperCase().replace("-", "_");
         Player var5 = Bukkit.getPlayer(var2[3]);
         Validate.notNull(var5, "Could not find player called '" + var2[3] + "'.");
         int var6 = Integer.parseInt(var2[4]);

         for (int var7 = 0; var7 < var5.getInventory().getSize() && var6 > 0; var7++) {
            ItemStack var8 = var5.getInventory().getItem(var7);
            if (var8 != null && var8.getType() != Material.AIR) {
               NBTItem var9 = NBTItem.get(var8);
               String var10 = var9.getType();
               if (var3.getId().equals(var10) && var9.getString("MMOITEMS_ITEM_ID").equals(var4)) {
                  int var11 = Math.min(var6, var8.getAmount());
                  var6 -= var11;
                  var8.setAmount(var8.getAmount() - var11);
                  var5.getInventory().setItem(var7, var8);
               }
            }
         }

         return CommandResult.SUCCESS;
      } catch (IllegalArgumentException var12) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var12.getMessage());
         return CommandResult.FAILURE;
      }
   }
}
