package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.droptable.item.MMOItemDropItem;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class DropCommandTreeNode extends CommandTreeNode {
   public DropCommandTreeNode(CommandTreeNode var1) {
      super(var1, "drop");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(new Parameter("<world>", (var0, var1x) -> Bukkit.getWorlds().forEach(var1xx -> var1x.add(var1xx.getName()))));
      this.addParameter(new Parameter("<x>", (var0, var1x) -> var1x.add("<x>")));
      this.addParameter(new Parameter("<y>", (var0, var1x) -> var1x.add("<y>")));
      this.addParameter(new Parameter("<z>", (var0, var1x) -> var1x.add("<z>")));
      this.addParameter(new Parameter("<drop-chance>", (var0, var1x) -> var1x.add("<drop-chance>")));
      this.addParameter(new Parameter("<min-max>", (var0, var1x) -> var1x.add("1-3")));
      this.addParameter(new Parameter("<unidentify-chance>", (var0, var1x) -> var1x.add("<unidentify-chance>")));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length != 10) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(var2[1])) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + var2[1].toUpperCase().replace("-", "_") + ".");
         var1.sendMessage(
            MMOItems.plugin.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type " + ChatColor.RED + "to see all the available item types."
         );
         return CommandResult.FAILURE;
      }

      Type var3 = Type.get(var2[1].toUpperCase());
      String var4 = var2[2].toUpperCase().replace("-", "_");
      FileConfiguration var5 = var3.getConfigFile().getConfig();
      if (!var5.contains(var4)) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item called " + var4 + ".");
         return CommandResult.FAILURE;
      }

      World var6 = Bukkit.getWorld(var2[3]);
      if (var6 == null) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Couldn't find the world called " + var2[3] + ".");
         return CommandResult.FAILURE;
      }

      double var7;
      try {
         var7 = Double.parseDouble(var2[4]);
      } catch (Exception var27) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var2[4] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      double var9;
      try {
         var9 = Double.parseDouble(var2[5]);
      } catch (Exception var26) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var2[5] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      double var11;
      try {
         var11 = Double.parseDouble(var2[6]);
      } catch (Exception var25) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var2[6] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      double var13;
      try {
         var13 = Double.parseDouble(var2[7]);
      } catch (Exception var24) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var2[7] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      double var15;
      try {
         var15 = Double.parseDouble(var2[9]);
      } catch (Exception var23) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var2[9] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      String[] var19 = var2[8].split("-");
      if (var19.length != 2) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "The drop quantity format is incorrect.");
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Format: [min]-[max]");
         return CommandResult.FAILURE;
      }

      int var17;
      try {
         var17 = Integer.parseInt(var19[0]);
      } catch (Exception var22) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var19[0] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      int var18;
      try {
         var18 = Integer.parseInt(var19[1]);
      } catch (Exception var21) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var19[1] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      ItemStack var20 = new MMOItemDropItem(var3, var4, var13 / 100.0, var15 / 100.0, var17, var18).getItem(null);
      if (var20 != null && var20.getType() != Material.AIR) {
         var6.dropItem(new Location(var6, var7, var9, var11), var20);
         return CommandResult.SUCCESS;
      } else {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "An error occurred while attempting to generate the item called " + var4 + ".");
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "See console for more information!");
         return CommandResult.FAILURE;
      }
   }
}
