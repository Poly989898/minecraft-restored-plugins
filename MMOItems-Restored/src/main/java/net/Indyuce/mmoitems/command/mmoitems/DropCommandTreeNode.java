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
   public DropCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "drop");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(new Parameter("<world>", (commandTreeExplorer, var1x) -> Bukkit.getWorlds().forEach(var1xx -> var1x.add(var1xx.getName()))));
      this.addParameter(new Parameter("<x>", (commandTreeExplorer, var1x) -> var1x.add("<x>")));
      this.addParameter(new Parameter("<y>", (commandTreeExplorer, var1x) -> var1x.add("<y>")));
      this.addParameter(new Parameter("<z>", (commandTreeExplorer, var1x) -> var1x.add("<z>")));
      this.addParameter(new Parameter("<drop-chance>", (commandTreeExplorer, var1x) -> var1x.add("<drop-chance>")));
      this.addParameter(new Parameter("<min-max>", (commandTreeExplorer, var1x) -> var1x.add("1-3")));
      this.addParameter(new Parameter("<unidentify-chance>", (commandTreeExplorer, var1x) -> var1x.add("<unidentify-chance>")));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length != 10) {
         return CommandResult.THROW_USAGE;
      }

      if (!Type.isValid(texts[1])) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item type called " + texts[1].toUpperCase().replace("-", "_") + ".");
         sender.sendMessage(
            MMOItems.plugin.getPrefix() + ChatColor.RED + "Type " + ChatColor.GREEN + "/mi list type " + ChatColor.RED + "to see all the available item types."
         );
         return CommandResult.FAILURE;
      }

      Type type = Type.get(texts[1].toUpperCase());
      String text = texts[2].toUpperCase().replace("-", "_");
      FileConfiguration config = type.getConfigFile().getConfig();
      if (!config.contains(text)) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "There is no item called " + text + ".");
         return CommandResult.FAILURE;
      }

      World world = Bukkit.getWorld(texts[3]);
      if (world == null) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Couldn't find the world called " + texts[3] + ".");
         return CommandResult.FAILURE;
      }

      double value;
      try {
         value = Double.parseDouble(texts[4]);
      } catch (Exception exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + texts[4] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      double dropChance;
      try {
         dropChance = Double.parseDouble(texts[5]);
      } catch (Exception exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + texts[5] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      double dropMin;
      try {
         dropMin = Double.parseDouble(texts[6]);
      } catch (Exception exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + texts[6] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      double dropMax;
      try {
         dropMax = Double.parseDouble(texts[7]);
      } catch (Exception exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + texts[7] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      double dropWeight;
      try {
         dropWeight = Double.parseDouble(texts[9]);
      } catch (Exception exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + texts[9] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      String[] amountRange = texts[8].split("-");
      if (amountRange.length != 2) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "The drop quantity format is incorrect.");
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Format: [min]-[max]");
         return CommandResult.FAILURE;
      }

      int count;
      try {
         count = Integer.parseInt(amountRange[0]);
      } catch (Exception exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + amountRange[0] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      int maxAmount;
      try {
         maxAmount = Integer.parseInt(amountRange[1]);
      } catch (Exception exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + amountRange[1] + " is not a valid number.");
         return CommandResult.FAILURE;
      }

      ItemStack item = new MMOItemDropItem(type, text, dropMax / 100.0, dropWeight / 100.0, count, maxAmount).getItem(null);
      if (item != null && item.getType() != Material.AIR) {
         world.dropItem(new Location(world, value, dropChance, dropMin), item);
         return CommandResult.SUCCESS;
      } else {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "An error occurred while attempting to generate the item called " + text + ".");
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "See console for more information!");
         return CommandResult.FAILURE;
      }
   }
}
