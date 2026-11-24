package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommandTreeNode extends CommandTreeNode {
   public InfoCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "info");
      this.addParameter(Parameter.PLAYER_OPTIONAL);
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      Player player = texts.length > 2 ? Bukkit.getPlayer(texts[2]) : (sender instanceof Player ? (Player)sender : null);
      if (player == null) {
         sender.sendMessage(ChatColor.RED + "Couldn't find target player.");
         return CommandResult.FAILURE;
      } else {
         RPGPlayer rPG = PlayerData.get(player).getRPG();
         sender.sendMessage(
            ChatColor.DARK_GRAY
               + ""
               + ChatColor.STRIKETHROUGH
               + "-----------------["
               + ChatColor.LIGHT_PURPLE
               + " Player Information "
               + ChatColor.DARK_GRAY
               + ""
               + ChatColor.STRIKETHROUGH
               + "]-----------------"
         );
         sender.sendMessage(ChatColor.WHITE + "Information about " + ChatColor.LIGHT_PURPLE + player.getName());
         sender.sendMessage("");
         sender.sendMessage(ChatColor.WHITE + "Player Class: " + ChatColor.LIGHT_PURPLE + rPG.getClassName());
         sender.sendMessage(ChatColor.WHITE + "Player Level: " + ChatColor.LIGHT_PURPLE + rPG.getLevel());
         sender.sendMessage(ChatColor.WHITE + "Player Mana: " + ChatColor.LIGHT_PURPLE + rPG.getMana());
         sender.sendMessage(ChatColor.WHITE + "Player Stamina: " + ChatColor.LIGHT_PURPLE + rPG.getStamina());
         return CommandResult.SUCCESS;
      }
   }
}
