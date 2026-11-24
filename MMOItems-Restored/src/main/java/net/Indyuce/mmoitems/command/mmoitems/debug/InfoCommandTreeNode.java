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
   public InfoCommandTreeNode(CommandTreeNode var1) {
      super(var1, "info");
      this.addParameter(Parameter.PLAYER_OPTIONAL);
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      Player var3 = var2.length > 2 ? Bukkit.getPlayer(var2[2]) : (var1 instanceof Player ? (Player)var1 : null);
      if (var3 == null) {
         var1.sendMessage(ChatColor.RED + "Couldn't find target player.");
         return CommandResult.FAILURE;
      } else {
         RPGPlayer var4 = PlayerData.get(var3).getRPG();
         var1.sendMessage(
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
         var1.sendMessage(ChatColor.WHITE + "Information about " + ChatColor.LIGHT_PURPLE + var3.getName());
         var1.sendMessage("");
         var1.sendMessage(ChatColor.WHITE + "Player Class: " + ChatColor.LIGHT_PURPLE + var4.getClassName());
         var1.sendMessage(ChatColor.WHITE + "Player Level: " + ChatColor.LIGHT_PURPLE + var4.getLevel());
         var1.sendMessage(ChatColor.WHITE + "Player Mana: " + ChatColor.LIGHT_PURPLE + var4.getMana());
         var1.sendMessage(ChatColor.WHITE + "Player Stamina: " + ChatColor.LIGHT_PURPLE + var4.getStamina());
         return CommandResult.SUCCESS;
      }
   }
}
