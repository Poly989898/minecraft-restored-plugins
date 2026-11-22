/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIMail;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.PlayerMail;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class mailall implements Cmd {
  private static volatile /* synthetic */ int[]
      $SWITCH_TABLE$com$Zrips$CMI$commands$list$mailall$Action;

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("sentMail", "{gcp}Sent {gcs}[amount] {gcp}mail(s)");
    configReader.get("clearMail", "{gcs}[amount] {gcp}mail(s) have been removed");
    configReader.get("removedMail", "{gcp}Removed {gcs}[amount] {gcp}mail(s)");
  }

  @Override
  @CAnnotation(
      info = "Check or send mail to all players",
      args = "[send/clear/remove] [message]",
      tab = {"send%%clear%%remove"},
      explanation = {},
      regVar = {-666},
      consoleVar = {-100},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    Enum enum_ = null;
    String string = "";
    String[] commandArgs = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      String arg = commandArgs[argIndex];
      switch (arg.toLowerCase()) {
        case "send":
          {
            enum_ = Action.Send;
            break;
          }
        case "clear":
          {
            enum_ = Action.Clear;
            break;
          }
        case "remove":
          {
            enum_ = Action.Remove;
            break;
          }
        default:
          {
            if (!string.isEmpty()) {
              string = String.valueOf(string) + " ";
            }
            string = String.valueOf(string) + arg;
          }
      }
      ++argIndex;
    }
    if (enum_ == null) {
      return false;
    }
    switch (mailall.$SWITCH_TABLE$com$Zrips$CMI$commands$list$mailall$Action()[enum_.ordinal()]) {
      case 1:
        {
          Object object;
          int sentCount = 0;
          PlayerMail playerMail = new PlayerMail(commandSender, string);
          for (Map.Entry entry : cMI.getPlayerManager().getAllUsers().entrySet()) {
            object = (CMIUser) entry.getValue();
            if (((CMIUser) object).getName() == null
                || ((CMIUser) object).getName().equals(commandSender.getName())
                || string.isEmpty()
                || ((CMIUser) object).getLastLogin() + CMIMail.getMailToAllExpirationMS()
                    < System.currentTimeMillis()) continue;
            ++sentCount;
            ((CMIUser) object).addMail(playerMail, true);
            Snd snd = new Snd().setSender(commandSender).setTarget((CMIUser) object);
            if (((CMIUser) object).isOnline()) {
              CMILC.info((Object) "mail", (CMIUser) object, "gotMail", snd);
            }
            ((CMIUser) object).addForDelayedSave();
          }
          CMILC.info((Object) this, commandSender, "sentMail", "[amount]", sentCount);
          break;
        }
      case 2:
        {
          Object object;
          int clearedCount = 0;
          for (Map.Entry entry : cMI.getPlayerManager().getAllUsers().entrySet()) {
            object = (CMIUser) entry.getValue();
            clearedCount += ((CMIUser) object).getMails().size();
            ((CMIUser) object).clearMails();
            ((CMIUser) object).addForDelayedSave();
          }
          CMILC.info((Object) this, commandSender, "clearMail", "[amount]", clearedCount);
          break;
        }
      case 3:
        {
          Object object;
          int removedCount = 0;
          block18:
          for (Map.Entry entry : cMI.getPlayerManager().getAllUsers().entrySet()) {
            object = (CMIUser) entry.getValue();
            List<PlayerMail> mails = ((CMIUser) object).getMails();
            if (mails.isEmpty()) continue;
            for (PlayerMail playerMail : new ArrayList<PlayerMail>(mails)) {
              if (!playerMail.getMessage().equalsIgnoreCase(string)) continue;
              ((CMIUser) object).removeMail(playerMail);
              ((CMIUser) object).addForDelayedSave();
              ++removedCount;
              continue block18;
            }
          }
          CMILC.info((Object) this, commandSender, "removedMail", "[amount]", removedCount);
          break;
        }
    }
    return true;
  }

  static /* synthetic */ int[] $SWITCH_TABLE$com$Zrips$CMI$commands$list$mailall$Action() {
    if ($SWITCH_TABLE$com$Zrips$CMI$commands$list$mailall$Action != null) {
      return $SWITCH_TABLE$com$Zrips$CMI$commands$list$mailall$Action;
    }
    int[] nArray = new int[Action.values().length];
    try {
      nArray[Action.Clear.ordinal()] = 2;
    } catch (NoSuchFieldError noSuchFieldError) {
    }
    try {
      nArray[Action.Remove.ordinal()] = 3;
    } catch (NoSuchFieldError noSuchFieldError) {
    }
    try {
      nArray[Action.Send.ordinal()] = 1;
    } catch (NoSuchFieldError noSuchFieldError) {
    }
    $SWITCH_TABLE$com$Zrips$CMI$commands$list$mailall$Action = nArray;
    return nArray;
  }

  private static enum Action {
    Send,
    Clear,
    Remove;
  }
}
