package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import java.text.DecimalFormat;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckAttributeCommandTreeNode extends CommandTreeNode {
   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.####");

   public CheckAttributeCommandTreeNode(CommandTreeNode var1) {
      super(var1, "checkattribute");
      this.addParameter(new Parameter("<attribute>", (var0, var1x) -> Arrays.asList(Attribute.values()).forEach(var1xx -> var1x.add(var1xx.name()))));
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      }

      if (var2.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      Player var3 = (Player)var1;

      try {
         AttributeInstance var4 = var3.getAttribute(Attribute.valueOf(var2[2].toUpperCase().replace("-", "_")));
         var1.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
         var1.sendMessage(ChatColor.AQUA + "Default Value = " + ChatColor.RESET + var4.getDefaultValue());
         var1.sendMessage(ChatColor.AQUA + "Base Value = " + ChatColor.RESET + var4.getBaseValue());
         var1.sendMessage(ChatColor.AQUA + "Value = " + ChatColor.RESET + var4.getValue());

         for (AttributeModifier var6 : var4.getModifiers()) {
            var1.sendMessage(var6.getName() + " " + DECIMAL_FORMAT.format(var6.getAmount()) + " " + var6.getOperation() + " " + var6.getSlot());
         }
      } catch (IllegalArgumentException var7) {
         var3.sendMessage("Couldn't find attribute.");
      }

      return CommandResult.SUCCESS;
   }
}
