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

   public CheckAttributeCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "checkattribute");
      this.addParameter(new Parameter("<attribute>", (commandTreeExplorer, texts) -> Arrays.asList(Attribute.values()).forEach(attribute -> texts.add(attribute.name()))));
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      }

      if (texts.length < 3) {
         return CommandResult.THROW_USAGE;
      }

      Player player = (Player)sender;

      try {
         AttributeInstance attributeInstance = player.getAttribute(Attribute.valueOf(texts[2].toUpperCase().replace("-", "_")));
         sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------------");
         sender.sendMessage(ChatColor.AQUA + "Default Value = " + ChatColor.RESET + attributeInstance.getDefaultValue());
         sender.sendMessage(ChatColor.AQUA + "Base Value = " + ChatColor.RESET + attributeInstance.getBaseValue());
         sender.sendMessage(ChatColor.AQUA + "Value = " + ChatColor.RESET + attributeInstance.getValue());

         for (AttributeModifier modifier : attributeInstance.getModifiers()) {
            sender.sendMessage(modifier.getName() + " " + DECIMAL_FORMAT.format(modifier.getAmount()) + " " + modifier.getOperation() + " " + modifier.getSlot());
         }
      } catch (IllegalArgumentException exception) {
         player.sendMessage("Couldn't find attribute.");
      }

      return CommandResult.SUCCESS;
   }
}
