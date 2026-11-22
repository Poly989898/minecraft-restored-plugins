package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIPlayerNickNameChangeEvent;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class nick implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "changed", "{gcp}NickName changed to &r[newNickName] {gcp}from &r[oldNickName]");
    configReader.get(
        "changedTarget",
        "{gcp}Your nickName changed to &r[newNickName] {gcp}by &r[senderDisplayName]");
    configReader.get("reset", "{gcp}Nick name set to default one");
    configReader.get(
        "sameNick",
        "{gcw}You don't have permission to change into different nick. You can change only colors");
    configReader.get("blockedNick", "{gcw}You can't use this nick name");
    configReader.get(
        "blockedNickLength", "{gcp}Nick name is to long. Max {gcs}[amount] {gcp}symbols");
    configReader.get(
        "blockedNickLengthMin", "{gcp}Nick name is to short. Min {gcs}[amount] {gcp}symbols");
    configReader.get("inUse", "{gcw}Nick name is already being used by another player");
  }

  @CAnnotation(
      priority = 48,
      info = "Changes player name",
      args = "[newNickName/off] (playerName) (-s)",
      tab = {"fullNickName%%off", "playerName", "-s"},
      explanation = {
        "To change into different nick name: cmi.command.nick.different",
        "To bypass length protection use cmi.command.nick.bypass.length"
      },
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String playerName = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else if (string == null) {
        string = arg;
      } else if (playerName == null) {
        playerName = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, playerName, this);
    if (cmiUser == null || string == null) {
      return null;
    }

    if (string.equalsIgnoreCase("off")) {
      cmiUser.setNickName(null, true);
      cmiUser.updateDisplayName(true);
      if (!flag) {
        CMILC.info(this, commandSender, "reset");
      }

      if (cmi.getTabListManager().isUpdatesOnNickChange()) {
        cmi.getTabListManager().updateTabList(3);
      }

      return true;
    } else {
      if (cmi.getChatManager().isColorsNickName()) {
        string =
            CMITextCleaner.cleanFromColorCodes(commandSender, string, CMIColorTypes.nickname, true);
      }

      String newNickName = CMIChatColor.stripColor(string);
      if (!newNickName.equalsIgnoreCase(cmiUser.getNickName())
          && !newNickName.equalsIgnoreCase(cmiUser.getName())
          && !PermissionsManager.CMIPerm.command_nick_different.hasPermission(commandSender)) {
        CMILC.info(this, commandSender, "sameNick");
        return null;
      }

      if ((!cmi.getNickNameManager().getNickNameBlackList().contains(newNickName.toLowerCase())
              || PermissionsManager.CMIPerm.command_nick_bypassblacklist.hasPermission(
                  commandSender))
          && (cmi.getUtilManager()
                  .validName(
                      cmi.getNickNameManager().getValidNickNameRegex(), newNickName.toLowerCase())
              || PermissionsManager.CMIPerm.command_nick_bypassblacklist.hasPermission(
                  commandSender))) {
        CMIUser nickOwner = cmi.getPlayerManager().getByName(newNickName);
        if (cmi.getNickNameManager().getPreventRealNames()
            && !PermissionsManager.CMIPerm.command_nick_bypassrealname.hasPermission(commandSender)
            && nickOwner != null
            && !nickOwner.getUniqueId().equals(cmiUser.getUniqueId())) {
          CMILC.info(this, commandSender, "inUse");
          return null;
        }

        if (cmi.getNickNameManager().getNickNameLengthMax() < newNickName.length()
            && !PermissionsManager.CMIPerm.command_nick_bypass_length.hasPermission(
                commandSender)) {
          CMILC.info(
              this,
              commandSender,
              "blockedNickLength",
              "[amount]",
              cmi.getNickNameManager().getNickNameLengthMax());
          return null;
        }

        if (!newNickName.isEmpty()
            && (cmi.getNickNameManager().getNickNameLengthMin() <= newNickName.length()
                || PermissionsManager.CMIPerm.command_nick_bypass_length.hasPermission(
                    commandSender))) {
          UUID uuid = cmi.getNickNameManager().whoUsesName(newNickName);
          if (uuid != null
              && !uuid.equals(cmiUser.getUniqueId())
              && !PermissionsManager.CMIPerm.command_nick_bypassinuse.hasPermission(
                  commandSender)) {
            CMILC.info(this, commandSender, "inUse");
            return null;
          }

          CMIPlayerNickNameChangeEvent cmiPlayerNickNameChangeEvent =
              new CMIPlayerNickNameChangeEvent(cmiUser, string);
          Bukkit.getPluginManager().callEvent(cmiPlayerNickNameChangeEvent);
          if (cmiPlayerNickNameChangeEvent.isCancelled()) {
            return null;
          }

          string = cmiPlayerNickNameChangeEvent.getNickName();
          cmiUser.setNickName(CMIChatColor.translate(string), true);
          if (!flag) {
            CMILC.info(
                this,
                commandSender,
                "changed",
                "[newNickName]",
                CMIChatColor.translate(string),
                "[oldNickName]",
                cmiUser.getDisplayName(false));
            if (!cmiUser.getName().equalsIgnoreCase(commandSender.getName())) {
              Snd snd = new Snd();
              snd.setSender(commandSender);
              snd.setTarget(cmiUser);
              CMILC.info(
                  this,
                  cmiUser,
                  "changedTarget",
                  "[newNickName]",
                  CMIChatColor.translate(string),
                  snd);
            }
          }

          cmiUser.updateDisplayName(true);
          if (cmi.getTabListManager().isUpdatesOnNickChange()) {
            cmi.getTabListManager().updateTabList(3);
          }

          return true;
        } else {
          CMILC.info(
              this,
              commandSender,
              "blockedNickLengthMin",
              "[amount]",
              cmi.getNickNameManager().getNickNameLengthMin());
          return null;
        }
      } else {
        CMILC.info(this, commandSender, "blockedNick");
        return null;
      }
    }
  }
}
