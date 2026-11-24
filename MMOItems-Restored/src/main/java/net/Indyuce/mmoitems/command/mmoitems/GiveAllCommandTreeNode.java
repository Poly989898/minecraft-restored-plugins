package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.api.util.SmartGive;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.droptable.item.MMOItemDropItem;
import net.Indyuce.mmoitems.api.util.RandomAmount;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveAllCommandTreeNode extends CommandTreeNode {
   public GiveAllCommandTreeNode(CommandTreeNode var1) {
      super(var1, "giveall");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(new Parameter("<min-max>", (var0, var1x) -> var1x.add("1-3")));
      this.addParameter(new Parameter("<unidentify-chance>", (var0, var1x) -> var1x.add("<unidentify-chance>")));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      try {
         Validate.isTrue(var2.length > 4, "Usage: /mi giveall <type> <item-id> <min-max> <unidentified-chance>");
         Type var3 = MMOItems.plugin.getTypes().getOrThrow(var2[1]);
         ItemStack var4 = new MMOItemDropItem(var3, var2[2], 1.0, Double.parseDouble(var2[4]) / 100.0, new RandomAmount(var2[3])).getItem(null);
         Validate.isTrue(
            var4 != null && var4.getType() != Material.AIR,
            "Couldn't find/generate the item called '" + var2[1].toUpperCase() + "'. Check your console for potential item generation issues."
         );

         for (Player var6 : Bukkit.getOnlinePlayers()) {
            new SmartGive(var6).give(new ItemStack[]{var4});
         }

         return CommandResult.SUCCESS;
      } catch (IllegalArgumentException var7) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var7.getMessage());
         return CommandResult.FAILURE;
      }
   }
}
