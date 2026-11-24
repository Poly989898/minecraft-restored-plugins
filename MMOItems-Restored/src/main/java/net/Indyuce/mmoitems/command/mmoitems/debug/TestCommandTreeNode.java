package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TestCommandTreeNode extends CommandTreeNode {
   public TestCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "test");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      ItemStack item = new ItemStack(Material.DIAMOND);
      ItemMeta itemMeta = item.getItemMeta();
      ArrayList list = new ArrayList();

      for (int i = 0; i < 30; i++) {
         list.add(ChatColor.WHITE + "ɴ" + UtilityMethods.getFontSpace(i) + "ɴ -> " + i);
      }

      itemMeta.setLore(list);
      item.setItemMeta(itemMeta);
      ((Player)sender).getInventory().addItem(new ItemStack[]{item});
      return CommandResult.SUCCESS;
   }
}
