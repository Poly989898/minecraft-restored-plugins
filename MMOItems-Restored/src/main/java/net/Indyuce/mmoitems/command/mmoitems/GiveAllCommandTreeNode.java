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
   public GiveAllCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "giveall");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(new Parameter("<min-max>", (commandTreeExplorer, var1x) -> var1x.add("1-3")));
      this.addParameter(new Parameter("<unidentify-chance>", (commandTreeExplorer, var1x) -> var1x.add("<unidentify-chance>")));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      try {
         Validate.isTrue(texts.length > 4, "Usage: /mi giveall <type> <item-id> <min-max> <unidentified-chance>");
         Type type = MMOItems.plugin.getTypes().getOrThrow(texts[1]);
         ItemStack item = new MMOItemDropItem(type, texts[2], 1.0, Double.parseDouble(texts[4]) / 100.0, new RandomAmount(texts[3])).getItem(null);
         Validate.isTrue(
            item != null && item.getType() != Material.AIR,
            "Couldn't find/generate the item called '" + texts[1].toUpperCase() + "'. Check your console for potential item generation issues."
         );

         for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            new SmartGive(onlinePlayer).give(new ItemStack[]{item});
         }

         return CommandResult.SUCCESS;
      } catch (IllegalArgumentException exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + exception.getMessage());
         return CommandResult.FAILURE;
      }
   }
}
