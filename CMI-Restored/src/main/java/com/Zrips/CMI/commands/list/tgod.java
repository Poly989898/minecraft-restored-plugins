package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIGod;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tgod implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "setTimedGod",
        "{gcp}You have set {gcs}[playerDisplayName]{gcp} temporary god for the next [time]");
    configReader.get(
        "setUntilRelogGod",
        "{gcp}You have set {gcs}[playerDisplayName]{gcp} temporary god until relog.");
    configReader.get(
        "targetTimedEnabled",
        "{gcp}You got temporary god mode enabled by {gcs}[senderDisplayName]{gcp} for the next"
            + " [time].");
    configReader.get(
        "targetUntilRelogEnabled",
        "{gcp}You got temporary god mode enabled by {gcs}[senderDisplayName]{gcp} until you relog");
    configReader.get("setFor", "{gcp}Temporary god mode enabled for the next [time]");
    configReader.get("setForRelog", "{gcp}Temporary god mode enabled until relog");
    configReader.get("notSet", "{gcp}Temporary god mode is not enabled for player");
    configReader.get("willBeDisabled", "{gcp}God mode will be disabled in [time]");
    configReader.get("disabled", "{gcp}Temporary god mode is disabled!");
  }

  @CAnnotation(
      info = "Set players temporarily god mode until relog or time end",
      args = "[playerName] (timeInSec) (-s)",
      multiTab = "[playerName] 20 -s",
      explanation = {
        "Examples:",
        "tgod Zrips 30 - god mode for the next 30 sec",
        "tgod Zrips 0 - god mode until relog",
        "tgod Zrips - check how long a player has tgod for, if at all."
      },
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      alias = true,
      modules = "timedcommands",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String string = null;
    Long time = null;
    tgod.Action action = tgod.Action.Set;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      if (text.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else if (string == null && cmi.getPlayerManager().getUser(text) != null) {
        string = cmi.getPlayerManager().getUser(text).getName();
      } else {
        try {
          if (text.startsWith("+")) {
            text = text.substring(1);
            action = tgod.Action.Add;
          } else if (text.startsWith("-")) {
            text = text.substring(1);
            action = tgod.Action.Take;
          }

          time = timeModifier.getTimeRangeFromString(text);
          if (time != null) {
            continue;
          }
        } catch (Exception exception) {
        }

        string = text;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (time != null && time == 0L) {
      action = tgod.Action.UntilRelog;
    }

    if (time == null) {
      if (cmiUser.getTgod() != 0L) {
        if (cmiUser.getTgod() > 0L) {
          long longValue = cmiUser.getTgod() - System.currentTimeMillis() + 1500L;
          if (longValue < 0L) {
            cmiUser.setTgod(0L);
            CMILC.info(this, commandSender, "notSet");
            return true;
          }

          CMILC.info(
              this, commandSender, "setFor", "[time]", CMITimeManager.to24hourShort(longValue));
        } else {
          CMILC.info(this, commandSender, "setForRelog");
        }
      } else {
        CMILC.info(this, commandSender, "notSet");
      }

      return true;
    } else {
      Player player = cmiUser.getPlayer();
      switch (action) {
        case Set:
          cmiUser.setTgod(System.currentTimeMillis() + time * 1000L);
          break;
        case Add:
          long timeSecond =
              CMINumber.clamp(cmiUser.getTgod(), System.currentTimeMillis(), Long.MAX_VALUE);
          cmiUser.setTgod(timeSecond + time * 1000L);
          break;
        case Take:
          long timeThird = cmiUser.getTgod() - System.currentTimeMillis() - time * 1000L;
          if (timeThird < 1L) {
            cmiUser.setTgod(0L);
          } else {
            cmiUser.setTgod(System.currentTimeMillis() + timeThird);
          }
          break;
        case UntilRelog:
          cmiUser.setTgod(-1L);
          time = 86400L;
      }

      CMIGod.forceApplyNoDamageTick(player, (int) (time * 20L));
      cmi.getTimedCommandManager().addTimed(cmiUser);
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      time = cmiUser.getTgod() == -1L ? -1L : cmiUser.getTgod() - System.currentTimeMillis();
      if (!flag) {
        if (time > 0L) {
          CMILC.info(
              this,
              commandSender,
              "setTimedGod",
              snd,
              "[time]",
              CMITimeManager.to24hourShort(time));
        } else if (time < -1L) {
          CMILC.info(this, commandSender, "disabled", snd);
        } else {
          CMILC.info(this, commandSender, "setUntilRelogGod", snd);
        }
      }

      if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
        if (time > 0L) {
          CMILC.info(
              this,
              player,
              "targetTimedEnabled",
              snd,
              "[time]",
              CMITimeManager.to24hourShort(time));
        } else if (time < -1L) {
          CMILC.info(this, player, "disabled", snd);
        } else {
          CMILC.info(this, player, "targetUntilRelogEnabled", snd);
        }
      }

      return true;
    }
  }

  enum Action {
    Set,
    Add,
    Take,
    UntilRelog;

    public tgod.Action get(String string) {
      tgod.Action[] actions;
      int value = (actions = values()).length;

      for (int count = 0; count < value; count++) {
        tgod.Action action = actions[count];
        if (action.toString().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
