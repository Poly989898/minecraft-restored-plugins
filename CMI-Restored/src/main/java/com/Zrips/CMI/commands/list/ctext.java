package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CustomText.CText;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashSet;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ctext implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcp}Custom Text's: {gcs}");
    configReader.get("check", "{gcp}Check: {gcs}[text]");
  }

  @CAnnotation(
      info = "Shows custom text",
      args = "[cText] (playerName/all) (sourcePlayer) (pageNumber)",
      tab = {"ctext", "playerName", "sourcePlayer"},
      regVar = -66,
      consoleVar = {2, 3, 4},
      paccess = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    Integer integer = 1;
    String playerArg = null;
    String userName = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      } else {
        try {
          integer = Integer.parseInt(arg);
        } catch (Exception exception) {
          if (playerArg == null) {
            playerArg = arg;
          } else {
            userName = arg;
          }
        }
      }
    }

    if (string == null) {
      RawMessage rawMessage = new RawMessage();
      rawMessage.addText(CMILC.getIM(this, "list"));

      for (var entry : cmi.getCTextManager().getAll().entrySet()) {
        if (!((CText) entry.getValue()).isHidden()
            && (!((CText) entry.getValue()).isRequirePermission()
                || PermissionsManager.CMIPerm.command_ctext_$1.hasPermission(
                    commandSender, ((CText) entry.getValue()).getName()))) {
          if (rawMessage.getFinalLenght() > 0) {
            rawMessage.addText(LC.info_ListSpliter.getLocale(new Object[0]));
          }

          rawMessage
              .addText(((CText) entry.getValue()).getName())
              .addHover(CMILC.getIM(this, "check", "[text]", ((CText) entry.getValue()).getName()))
              .addCommand("cmi ctext " + ((CText) entry.getValue()).getName());
        }
      }

      rawMessage.show(commandSender);
      return true;
    } else {
      CText cText = cmi.getCTextManager().getCText(string);
      if (cText == null) {
        CMILC.info("editctext", commandSender, "noCText", "[name]", string);
        return null;
      }

      if (cText.isRequirePermission()
          && !PermissionsManager.CMIPerm.command_ctext_$1.hasPermission(
              commandSender, cText.getName())) {
        LC.info_NoPermission.sendMessage(commandSender, new Object[0]);
        return null;
      }

      CMIUser cmiUser = cmi.getPlayerManager().getUser(userName);
      if (playerArg == null || !playerArg.equalsIgnoreCase("all")) {
        Player player = cmi.getTarget(commandSender, playerArg, this);
        if (player == null) {
          return null;
        }

        if (!player.isOnline()) {
          LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
          return null;
        }

        if (strings.length == 1) {
          cmi.getCTextManager().showCText(player, cmiUser, cText, 1);
          return true;
        }

        if (strings.length < 2) {
          return false;
        }

        cmi.getCTextManager().showCText(player, cmiUser, cText, integer);
        return true;
      } else {
        if (!PermissionsManager.CMIPerm.command_$1_others.hasPermission(
            commandSender, true, this.getClass().getSimpleName())) {
          return null;
        }

        HashSet<Player> hashSet = new HashSet();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          hashSet.add(onlinePlayer);
        }

        cmi.getCTextManager().showCText(hashSet, cmiUser, cText, integer);
        return true;
      }
    }
  }
}
