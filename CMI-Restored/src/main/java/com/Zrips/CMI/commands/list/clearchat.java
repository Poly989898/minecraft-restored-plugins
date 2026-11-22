package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class clearchat implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("information", "{gcp}Chat was cleared by {gcs}[senderDisplayName]");
  }

  @CAnnotation(
      info = "Clears chat",
      args = "(self) (-s)",
      explanation = {"Permission:", " cmi.command.clearchat.bypass - to ignore chat cleaning"},
      multiTab = "self,-s !-s",
      regVar = {0, 1},
      consoleVar = 1,
      customAlias = "cc",
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    boolean silent = false;
    ArrayList<Player> arrayList = new ArrayList();
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (string.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          silent = true;
        }
      } else if (string.equalsIgnoreCase("self")) {
        flag = true;
      }
    }

    if (flag) {
      if (commandSender instanceof Player) {
        arrayList.add((Player) commandSender);
      }
    } else {
      arrayList.addAll(Bukkit.getOnlinePlayers());
    }

    String clearText = " ";

    for (int lineCount = 0; lineCount < 100; lineCount++) {
      clearText = clearText + " \n";
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(commandSender);

    for (Player player : arrayList) {
      if (PermissionsManager.CMIPerm.command_clearchat_bypass.hasPermission(player)
          && !flag
          && !silent) {
        CMILC.info(this, player, "information", snd);
      } else {
        player.sendMessage(clearText);
        if (!silent) {
          CMILC.info(this, player, "information", snd);
        }
      }
    }

    return true;
  }
}
