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

   public GiveCommandTreeNode(CommandTreeNode var1) {
      super(var1, "give");
      this.addParameter(MMOItemsCommandTreeRoot.TYPE);
      this.addParameter(MMOItemsCommandTreeRoot.ID_2);
      this.addParameter(Parameter.PLAYER_OPTIONAL);
      this.addParameter(new Parameter("(min-max)", (var0, var1x) -> var1x.addAll(Arrays.asList("1-3", "1", "10", "32", "64"))));
      this.addParameter(new Parameter("(unidentify-chance)", (var0, var1x) -> var1x.add("(unidentify-chance)")));
      this.addParameter(new Parameter("(drop-chance)", (var0, var1x) -> var1x.add("(drop-chance)")));
      this.addParameter(new Parameter("(soulbound-chance)", (var0, var1x) -> var1x.add("(soulbound-chance)")));
      this.addParameter(new Parameter("(silent)", (var0, var1x) -> var1x.addAll(Arrays.asList("silent", "s"))));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      try {
         Validate.isTrue(var2.length > 3 || var1 instanceof Player, "Please specify a player.");
         Player var3 = var2.length > 3 ? Bukkit.getPlayer(var2[3]) : (Player)var1;
         Validate.notNull(var3, "Could not find player called '" + var2[var2.length > 3 ? 3 : 2] + "'.");
         Type var4 = MMOItems.plugin.getTypes().getOrThrow(var2[1].toUpperCase().replace("-", "_"));
         MMOItemTemplate var5 = MMOItems.plugin.getTemplates().getTemplateOrThrow(var4, var2[2].toUpperCase().replace("-", "_"));
         RandomAmount var6 = var2.length > 4 ? new RandomAmount(var2[4]) : new RandomAmount(1, 1);
         double var7 = var2.length > 5 ? Double.parseDouble(var2[5]) / 100.0 : 0.0;
         double var9 = var2.length > 6 ? Double.parseDouble(var2[6]) / 100.0 : 1.0;
         double var11 = var2.length > 7 ? Double.parseDouble(var2[7]) / 100.0 : 0.0;
         boolean var13 = var2.length > 8 && (var2[8].equalsIgnoreCase("silent") || var2[8].equalsIgnoreCase("s"));
         if (random.nextDouble() > var9) {
            return CommandResult.SUCCESS;
         }

         MMOItem var14 = var5.newBuilder(PlayerData.get(var3).getRPG()).build();
         if (random.nextDouble() < var11) {
            var14.setData(ItemStats.SOULBOUND, new SoulboundData(var3, 1));
         }

         ItemStack var15 = var14.newBuilder().build();
         if (random.nextDouble() < var7) {
            var15 = var4.getUnidentifiedTemplate().newBuilder(NBTItem.get(var15)).build();
         }

         Validate.isTrue(
            var15 != null && var15.getType() != Material.AIR,
            "Couldn't find/generate the item called '" + var5.getId() + "'. Check your console for potential item generation issues."
         );
         var15.setAmount(var6.getRandomAmount());
         if (!var13) {
            Message.RECEIVED_ITEM
               .format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var15), "#amount#", var15.getAmount() > 1 ? " x" + var15.getAmount() : "")
               .send(var3);
            if (!var1.equals(var3)) {
               var1.sendMessage(
                  MMOItems.plugin.getPrefix()
                     + ChatColor.YELLOW
                     + "Successfully gave "
                     + ChatColor.GOLD
                     + MMOUtils.getDisplayName(var15)
                     + (var15.getAmount() > 1 ? " x" + var15.getAmount() : "")
                     + ChatColor.YELLOW
                     + " to "
                     + ChatColor.GOLD
                     + var3.getName()
                     + ChatColor.YELLOW
                     + "."
               );
            }
         }

         new SmartGive(var3).give(new ItemStack[]{var15});
         return CommandResult.SUCCESS;
      } catch (IllegalArgumentException var16) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + var16.getMessage());
         return CommandResult.FAILURE;
      }
   }
}
