/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMINumber
 *  net.Zrips.CMILib.Container.CMIPlayer
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.Version.Version
 *  org.bukkit.Bukkit
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
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIPlayer;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class maxhp implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set {gcs}[maxHp] {gcp}max hp for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback",
        "{gcp}Your max HP was set to {gcs}[maxHp] {gcp}by {gcs}[senderDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      info = "Set a player's max hp",
      args = "[set/add/take/clear] [playerName] [amount] (-s)",
      tab = {"set%%add%%take%%clear", "playername"},
      explanation = {},
      regVar = {1, 2, 3, 4},
      consoleVar = {2, 3, 4},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String string;
    boolean bl = false;
    Action action = Action.Set;
    double d = 0.0;
    String amountText = null;
    String targetName = null;
    double maxLimit = Double.MAX_VALUE;
    if (Version.isSpigot()) {
      try {
        maxLimit = Bukkit.spigot().getConfig().getDouble("settings.attribute.maxHealth.max");
        if (maxLimit <= 0.0) {
          maxLimit = Double.MAX_VALUE;
        }
      } catch (Throwable throwable) {
        // empty catch block
      }
    }
    String[] args = stringArray;
    int n = stringArray.length;
    int count = 0;
    while (count < n) {
      string = args[count];
      switch (string.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        case "add":
          {
            action = Action.Add;
            break;
          }
        case "set":
          {
            action = Action.Set;
            break;
          }
        case "take":
          {
            action = Action.Take;
            break;
          }
        case "clear":
          {
            action = Action.Clear;
            break;
          }
        default:
          {
            Player matchedPlayer;
            if (targetName == null
                && (matchedPlayer = CMIUser.getOnlinePlayer(commandSender, string, true)) != null) {
              targetName = string;
              break;
            }
            try {
              double parsedValue = Double.parseDouble(string);
              if (!(parsedValue <= maxLimit)) break;
              d = parsedValue;
              break;
            } catch (NumberFormatException numberFormatException) {
              if (cMI.getPlaceholderAPIManager().containsPlaceHolder(string)) {
                amountText = string;
                break;
              }
              targetName = string;
            }
          }
      }
      ++count;
    }
    Player target = cMI.getTarget(commandSender, targetName, this);
    if (target == null) {
      return null;
    }
    if (d == 0.0 && amountText != null) {
      amountText = cMI.getPlaceholderAPIManager().updatePlaceHolders(target, amountText);
      try {
        count = (int) Double.parseDouble(amountText);
        if ((double) count <= maxLimit) {
          d = count;
        }
      } catch (NumberFormatException numberFormatException) {
        // empty catch block
      }
    }
    if (d < 1.0 && action != Action.Clear) {
      LC.info_NoLessThan.sendMessage((Object) commandSender, new Object[] {"[amount]", 1});
      return null;
    }
    maxLimit = (int) Math.min((double) cMI.getConfigManager().getMaxHp(), maxLimit);
    if (d > maxLimit) {
      LC.info_NoMoreThan.sendMessage((Object) commandSender, new Object[] {"[amount]", maxLimit});
      return null;
    }
    double oldMaxHealth = CMIPlayer.getMaxHealth(target);
    double newHealth = 20.0;
    double currentHealth = target.getHealth();
    switch (action) {
      case Add:
        {
          newHealth = CMINumber.clamp((double) (oldMaxHealth + d), (double) 1.0, (double) maxLimit);
          break;
        }
      case Set:
        {
          newHealth = CMINumber.clamp((double) d, (double) 1.0, (double) maxLimit);
          break;
        }
      case Take:
        {
          newHealth = CMINumber.clamp((double) (oldMaxHealth - d), (double) 1.0, (double) maxLimit);
          break;
        }
    }
    CMIPlayer.setMaxHealth(target, (double) newHealth);
    double appliedMaxHealth = CMIPlayer.getMaxHealth(target);
    if (appliedMaxHealth < oldMaxHealth) {
      if (currentHealth > appliedMaxHealth) {
        target.setHealth(appliedMaxHealth);
      }
    } else {
      newHealth = appliedMaxHealth < currentHealth + d ? appliedMaxHealth : currentHealth + d;
      newHealth = CMINumber.clamp((double) newHealth, (double) 1.0, (double) appliedMaxHealth);
      target.setHealth(newHealth);
    }
    cMI.save(target);
    Snd snd = new Snd().setSender(commandSender).setTarget(target);
    CMILC.info((Object) this, commandSender, "feedback", snd, "[amount]", appliedMaxHealth);
    if (!target.getName().equalsIgnoreCase(commandSender.getName()) && !bl) {
      CMILC.info((Object) this, target, "targetfeedback", snd, "[amount]", appliedMaxHealth);
    }
    return true;
  }

  private static enum Action {
    Set,
    Add,
    Take,
    Clear;
  }
}
