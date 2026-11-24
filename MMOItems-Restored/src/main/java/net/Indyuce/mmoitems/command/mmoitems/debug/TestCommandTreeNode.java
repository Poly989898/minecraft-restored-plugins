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
   public TestCommandTreeNode(CommandTreeNode var1) {
      super(var1, "test");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      ItemStack var3 = new ItemStack(Material.DIAMOND);
      ItemMeta var4 = var3.getItemMeta();
      ArrayList var5 = new ArrayList();

      for (int var6 = 0; var6 < 30; var6++) {
         var5.add(ChatColor.WHITE + "ɴ" + UtilityMethods.getFontSpace(var6) + "ɴ -> " + var6);
      }

      var4.setLore(var5);
      var3.setItemMeta(var4);
      ((Player)var1).getInventory().addItem(new ItemStack[]{var3});
      return CommandResult.SUCCESS;
   }
}
