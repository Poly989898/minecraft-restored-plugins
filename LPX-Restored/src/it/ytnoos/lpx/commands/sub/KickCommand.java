package it.ytnoos.lpx.commands.sub;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.commands.SubCommand;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends SubCommand {
   public KickCommand(LPXBukkit lpxBukkit2) {
      super(lpxBukkit2, "kick", "lpx.kick", "Force close player connection", false, "<player> <message>");
   }

   private static String joinMessage(String[] strings) {
      String[] strings2 = strings;
      StringBuilder stringBuilder = new StringBuilder();

      for (int value = 2; value < strings2.length; value++) {
         stringBuilder.append(strings2[value]);
         if (value != strings2.length - 1) {
            stringBuilder.append(" ");
         }
      }

      return stringBuilder.toString();
   }

   public static void runCommand(LPXBukkit lpxBukkit2, CommandSender commandSender, String[] strings) {
      String[] strings2 = strings;
      if (strings2.length < 2) {
         MessageFormatter.send(commandSender, lpxBukkit2.getConfigManager().getMessagesConfig().getPrefix() + ChatColor.RED + " Syntax: /lpx kick <player> <message>");
      } else {
         Player player = Bukkit.getPlayerExact(strings2[1]);
         if (player == null) {
            MessageFormatter.send(commandSender, lpxBukkit2.getConfigManager().getMessagesConfig().getPrefix() + ChatColor.RED + " " + strings2[1] + " not found!");
         } else {
            String string;
            if (strings2.length > 2) {
               string = joinMessage(strings2);
            } else {
               string = lpxBukkit2.getConfigManager().getMessagesConfig().getKick();
            }

            lpxBukkit2.getLogger().info(() -> "Force kicking " + player.getName() + "...");
            lpxBukkit2.kick(player, string);
         }
      }
   }

   @Override
   public void execute(CommandSender commandSender, String[] strings) {
      String[] strings2 = strings;
      runCommand(this.lpxBukkit, commandSender, strings2);
   }

   @Override
   public List tabComplete(CommandSender commandSender, String[] strings) {
      String[] strings2 = strings;
      if (strings2.length != 2) {
         return List.of();
      }

      ArrayList arrayList = new ArrayList();
      String string = strings2[1];

      for (Player player : Bukkit.getOnlinePlayers()) {
         if (!(commandSender instanceof Player player2 && !player2.canSee(player)) && player.getName().regionMatches(true, 0, string, 0, string.length())) {
            arrayList.add(player.getName());
         }
      }

      return arrayList;
   }

}
