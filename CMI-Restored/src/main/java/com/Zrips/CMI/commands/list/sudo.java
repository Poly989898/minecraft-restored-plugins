package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class sudo implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "performed",
        "{gcp}Performed '{gcs}[command]{gcp}' command as {gcs}[playerDisplayName] {gcp}player");
  }

  @CAnnotation(
      priority = 70,
      info = "Force another player to perform command",
      args = "[playerName] (command/c:[text])",
      tab = {"playername", "c:"},
      explanation = "cmi.command.sudo.bypass - protects player from being 'trolled'",
      regVar = {-100, -1},
      consoleVar = {-100, -1},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = CMIUser.getOnlinePlayer(strings[0]);
    if (player != null
        && (!(commandSender instanceof Player) || ((Player) commandSender).canSee(player))) {
      StringBuilder stringBuilder = new StringBuilder();
      if (strings.length > 1) {
        for (int value = 1; value < strings.length; value++) {
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append(" ");
          }

          stringBuilder.append(strings[value]);
        }
      }

      String string = stringBuilder.toString();
      if (PermissionsManager.CMIPerm.command_sudo_bypass.hasPermission(player)) {
        Snd snd = new Snd().setSender(commandSender).setTarget(player);
        LC.info_cantDoForPlayer.sendMessage(commandSender, new Object[] {snd});
        return null;
      }

      if (string.startsWith("c:")) {
        string = string.substring(2, string.length());
        Bukkit.getConsoleSender().sendMessage(string);
        player.chat(CMIChatColor.translate(string));
      } else {
        if (!string.startsWith("/")) {
          string = "/" + string;
        }

        PlayerCommandPreprocessEvent playerCommandPreprocessEvent =
            new PlayerCommandPreprocessEvent(player, string);
        Bukkit.getServer().getPluginManager().callEvent(playerCommandPreprocessEvent);
        if (!playerCommandPreprocessEvent.isCancelled()) {
          Snd snd = new Snd().setSender(commandSender).setTarget(player);
          CMILC.info(this, commandSender, "performed", "[command]", string, snd);
          string = playerCommandPreprocessEvent.getMessage();
          if (string.startsWith("/")) {
            string = string.substring(1, string.length());
          }

          cmi.performCommand(player, string);
        }
      }

      return true;
    } else {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
