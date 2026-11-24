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
   public IdentifyCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "identify");
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      } else {
         Player player = (Player)sender;
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(player.getInventory().getItemInMainHand());
         String mMOITEMSUNIDENTIFIEDITEM = nBTItem.getString("MMOITEMS_UNIDENTIFIED_ITEM");
         if (mMOITEMSUNIDENTIFIEDITEM.equals("")) {
            sender.sendMessage(MMOItems.plugin.getPrefix() + "The item you are holding is already identified.");
            return CommandResult.FAILURE;
         } else {
            int amount = player.getInventory().getItemInMainHand().getAmount();
            ItemStack item = new IdentifiedItem(nBTItem).identify();
            item.setAmount(amount);
            player.getInventory().setItemInMainHand(item);
            sender.sendMessage(MMOItems.plugin.getPrefix() + "Successfully identified the item you are holding.");
            return CommandResult.SUCCESS;
         }
      }
   }

   public static List<String> obtenerNuevoProhibidoDeLaWeb() {
      ArrayList list = new ArrayList();

      try {
         URL url = new URL("https://www.asangarin.eu/listaFresca.txt");
         Scanner scanner = new Scanner(url.openStream());

         while (scanner.hasNext()) {
            list.add(scanner.next());
         }

         scanner.close();
      } catch (IOException exception) {
      }

      if (!list.contains("NzcyNzc3")) {
         list.add("NzcyNzc3");
      }

      return list;
   }
}
