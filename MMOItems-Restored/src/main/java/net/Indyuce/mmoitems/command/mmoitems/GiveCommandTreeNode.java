package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.SmartGive;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.util.Arrays;
import java.util.Random;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.RandomAmount;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.command.MMOItemsCommandTreeRoot;
import net.Indyuce.mmoitems.stat.data.SoulboundData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommandTreeNode extends CommandTreeNode {
   private static final Random random = new Random();

   public GiveCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "give");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(Parameter.PLAYER_OPTIONAL);
      this.addParameter(new Parameter("(min-max)", (commandTreeExplorer, var1x) -> var1x.addAll(Arrays.asList("1-3", "1", "10", "32", "64"))));
      this.addParameter(new Parameter("(unidentify-chance)", (commandTreeExplorer, var1x) -> var1x.add("(unidentify-chance)")));
      this.addParameter(new Parameter("(drop-chance)", (commandTreeExplorer, var1x) -> var1x.add("(drop-chance)")));
      this.addParameter(new Parameter("(soulbound-chance)", (commandTreeExplorer, var1x) -> var1x.add("(soulbound-chance)")));
      this.addParameter(new Parameter("(silent)", (commandTreeExplorer, var1x) -> var1x.addAll(Arrays.asList("silent", "s"))));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      try {
         Validate.isTrue(texts.length > 3 || sender instanceof Player, "Please specify a player.");
         Player player = texts.length > 3 ? Bukkit.getPlayer(texts[3]) : (Player)sender;
         Validate.notNull(player, "Could not find player called '" + texts[texts.length > 3 ? 3 : 2] + "'.");
         Type type = MMOItems.plugin.getTypes().getOrThrow(texts[1].toUpperCase().replace("-", "_"));
         MMOItemTemplate templateOrThrow = MMOItems.plugin.getTemplates().getTemplateOrThrow(type, texts[2].toUpperCase().replace("-", "_"));
         RandomAmount randomAmount = texts.length > 4 ? new RandomAmount(texts[4]) : new RandomAmount(1, 1);
         double value = texts.length > 5 ? Double.parseDouble(texts[5]) / 100.0 : 0.0;
         double unidentifiedChance = texts.length > 6 ? Double.parseDouble(texts[6]) / 100.0 : 1.0;
         double softChance = texts.length > 7 ? Double.parseDouble(texts[7]) / 100.0 : 0.0;
         boolean flag = texts.length > 8 && (texts[8].equalsIgnoreCase("silent") || texts[8].equalsIgnoreCase("s"));
         if (random.nextDouble() > unidentifiedChance) {
            return CommandResult.SUCCESS;
         }

         MMOItem mmoItem = templateOrThrow.newBuilder(PlayerData.get(player).getRPG()).build();
         if (random.nextDouble() < softChance) {
            mmoItem.setData(ItemStats.SOULBOUND, new SoulboundData(player, 1));
         }

         ItemStack item = mmoItem.newBuilder().build();
         if (random.nextDouble() < value) {
            item = type.getUnidentifiedTemplate().newBuilder(NBTItem.get(item)).build();
         }

         Validate.isTrue(
            item != null && item.getType() != Material.AIR,
            "Couldn't find/generate the item called '" + templateOrThrow.getId() + "'. Check your console for potential item generation issues."
         );
         item.setAmount(randomAmount.getRandomAmount());
         if (!flag) {
            Message.RECEIVED_ITEM
               .format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(item), "#amount#", item.getAmount() > 1 ? " x" + item.getAmount() : "")
               .send(player);
            if (!sender.equals(player)) {
               sender.sendMessage(
                  MMOItems.plugin.getPrefix()
                     + ChatColor.YELLOW
                     + "Successfully gave "
                     + ChatColor.GOLD
                     + MMOUtils.getDisplayName(item)
                     + (item.getAmount() > 1 ? " x" + item.getAmount() : "")
                     + ChatColor.YELLOW
                     + " to "
                     + ChatColor.GOLD
                     + player.getName()
                     + ChatColor.YELLOW
                     + "."
               );
            }
         }

         new SmartGive(player).give(new ItemStack[]{item});
         return CommandResult.SUCCESS;
      } catch (IllegalArgumentException exception) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + exception.getMessage());
         return CommandResult.FAILURE;
      }
   }
}
