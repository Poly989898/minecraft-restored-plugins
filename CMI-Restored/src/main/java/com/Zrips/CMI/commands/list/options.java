package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class options implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("title", "{gcn}Options");
    configReader.get("state", "{gcn}State: {gcs}[state]");
    configReader.get("feedback", "{gcs}[description]{gcp}: {gcs}[state]");
    PlayerOption[] playerOptions;
    int value = (playerOptions = PlayerOption.values()).length;

    for (int index = 0; index < value; index++) {
      PlayerOption playerOption = playerOptions[index];
      playerOption.setDesc(
          configReader.get("option." + playerOption.name(), playerOption.getDesc()));
    }
  }

  @CAnnotation(
      priority = 40,
      info = "Modify personal options",
      args = "(playerName) (option) (enable/disable/toggle/status) (-s)",
      multiTab =
          "[playerName],[playerOption],!enable,!disable,!toggle,!status,-s"
              + " !enable,[playerOption],!disable,!toggle,!status,!-s"
              + " !enable,[playerOption],!disable,!toggle,!status,!-s !-s",
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      others = true,
      explanation =
          "Possible actions: visibleHolograms, shiftSignEdit, totemBossBar, bossBarCompass,"
              + " tagSound, chatSpy, cmdSpy, signSpy, acceptingPM, acceptingTPA, acceptingMoney,"
              + " chatbubble, pmsound, deathmessages")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    PlayerOption playerOption = null;
    options.Actions actions = null;
    String string = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (playerOption == null) {
        playerOption = PlayerOption.get(arg);
        if (playerOption != null) {
          continue;
        }
      }

      if (arg.equalsIgnoreCase("true")
          || arg.equalsIgnoreCase("enable")
          || arg.equalsIgnoreCase("enabled")) {
        actions = options.Actions.enable;
      } else if (arg.equalsIgnoreCase("false")
          || arg.equalsIgnoreCase("disable")
          || arg.equalsIgnoreCase("disabled")) {
        actions = options.Actions.disable;
      } else if (arg.equalsIgnoreCase("status")) {
        actions = options.Actions.status;
      } else if (arg.equalsIgnoreCase("toggle")) {
        actions = options.Actions.toggle;
      } else if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        string = arg;
      }
    }

    if (actions == null) {
      if (playerOption != null) {
        actions = options.Actions.status;
      } else {
        actions = options.Actions.gui;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (playerOption == null || actions == options.Actions.gui) {
      cmi.getPlayerOptionsManager()
          .openOptionGUI(
              commandSender instanceof Player ? (Player) commandSender : cmiUser.getPlayer(),
              cmiUser);
      return true;
    }

    if (actions == options.Actions.status) {
      Boolean optionState = cmiUser.getOptionState(playerOption);
      CMILC.info(
          this,
          commandSender,
          "feedback",
          "[description]",
          playerOption.getDesc(),
          "[state]",
          optionState);
      return true;
    }

    if (!PermissionsManager.CMIPerm.command_options_$1.hasPermission(
        commandSender, true, playerOption.name())) {
      return false;
    }

    if (actions == options.Actions.toggle) {
      cmiUser.setOptionState(playerOption, !cmiUser.getOptionState(playerOption));
    } else {
      cmiUser.setOptionState(playerOption, actions == options.Actions.enable);
    }

    if (!flag) {
      CMILC.info(
          this,
          commandSender,
          "feedback",
          "[description]",
          playerOption.getDesc(),
          "[state]",
          cmiUser.getOptionState(playerOption));
    }

    return true;
  }

  private enum Actions {
    enable,
    disable,
    status,
    toggle,
    gui;
  }
}
