/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.CMILib
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  org.bukkit.GameMode
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gm implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set {gcs}[playerDisplayName]{gcp} to gamemode {gcs}[gameMode]{gcp}.");
    configReader.get(
        "targetfeedback",
        "{gcp}Your game mode was set to {gcs}[gameMode]{gcp} by {gcs}[senderDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      info = "Set players game mode",
      args = "(playerName) [gamemode]",
      multiTab = {"![playername],![gamemode] ![gamemode],![playername],-s -s"},
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      customAlias = {
        "gamemode",
        "+creative:gmc",
        "+survival:gms",
        "+spectator:gmsp",
        "+adventure:gma"
      },
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Object object;
    boolean bl = false;
    GameMode gameMode = null;
    Object targetName = null;
    String[] commandArgs = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      object = commandArgs[argIndex];
      switch (((String) object).toLowerCase()) {
        case "survival":
        case "0":
        case "s":
          {
            gameMode = GameMode.SURVIVAL;
            break;
          }
        case "1":
        case "c":
        case "creative":
          {
            gameMode = GameMode.CREATIVE;
            break;
          }
        case "adventure":
        case "2":
        case "a":
          {
            gameMode = GameMode.ADVENTURE;
            break;
          }
        case "spectator":
        case "3":
        case "sp":
          {
            gameMode = GameMode.SPECTATOR;
            break;
          }
        default:
          {
            if (((String) object).equalsIgnoreCase("-s")
                && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
              bl = true;
              break;
            }
            targetName = object;
          }
      }
      ++argIndex;
    }
    if (gameMode == null) {
      CMILC.info_NoGameMode.sendMessage(commandSender, new Object[0]);
      return null;
    }
    object = cMI.getUser(commandSender, (String) targetName, this, false);
    if (object == null) {
      return null;
    }
    Player player = ((CMIUser) object).getPlayer();
    if (!PermissionsManager.CMIPerm.command_gm_$1.hasPermission(
        commandSender, true, gameMode.name().toLowerCase())) {
      return null;
    }
    if (!commandSender.getName().equalsIgnoreCase(player.getName())
        && !PermissionsManager.CMIPerm.command_$1_others_$2.hasPermission(
            commandSender, true, this.getClass().getSimpleName(), gameMode.name().toLowerCase())) {
      return null;
    }
    cMI.getReflectionManager().setGameMode(player, gameMode);
    if (gameMode == GameMode.CREATIVE || gameMode.name().equalsIgnoreCase("SPECTATOR")) {
      ((CMIUser) object).setTfly(0L);
    }
    cMI.save(player);
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (!bl) {
      CMILC.info(
          (Object) this,
          commandSender,
          "feedback",
          snd,
          "[mode]",
          CMILib.getInstance()
              .getLM()
              .getMessage("info.variables." + gameMode.name().toLowerCase(), new Object[0]));
      if (!player.getName().equalsIgnoreCase(commandSender.getName())) {
        CMILC.info(
            (Object) this,
            (CommandSender) player,
            "targetfeedback",
            snd,
            "[mode]",
            CMILib.getInstance()
                .getLM()
                .getMessage("info.variables." + gameMode.name().toLowerCase(), new Object[0]));
      }
    }
    return true;
  }
}
