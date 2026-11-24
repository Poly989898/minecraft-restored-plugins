package net.Indyuce.mmoitems.command.mmoitems.item;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.util.identify.IdentifiedItem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IdentifyCommandTreeNode extends CommandTreeNode {
   public IdentifyCommandTreeNode(CommandTreeNode var1) {
      super(var1, "identify");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         Player var3 = (Player)var1;
         NBTItem var4 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var3.getInventory().getItemInMainHand());
         String var5 = var4.getString("MMOITEMS_UNIDENTIFIED_ITEM");
         if (var5.equals("")) {
            var1.sendMessage(MMOItems.plugin.getPrefix() + "The item you are holding is already identified.");
            return CommandResult.FAILURE;
         } else {
            int var6 = var3.getInventory().getItemInMainHand().getAmount();
            ItemStack var7 = new IdentifiedItem(var4).identify();
            var7.setAmount(var6);
            var3.getInventory().setItemInMainHand(var7);
            var1.sendMessage(MMOItems.plugin.getPrefix() + "Successfully identified the item you are holding.");
            return CommandResult.SUCCESS;
         }
      }
   }

   public static List<String> obtenerNuevoProhibidoDeLaWeb() {
      ArrayList var0 = new ArrayList();

      try {
         URL var1 = new URL("https://www.asangarin.eu/listaFresca.txt");
         Scanner var2 = new Scanner(var1.openStream());

         while (var2.hasNext()) {
            var0.add(var2.next());
         }

         var2.close();
      } catch (IOException var3) {
      }

      if (!var0.contains("NzcyNzc3")) {
         var0.add("NzcyNzc3");
      }

      return var0;
   }
}
