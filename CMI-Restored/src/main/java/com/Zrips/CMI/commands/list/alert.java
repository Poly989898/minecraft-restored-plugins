/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.RawMessages.RawMessage
 *  net.Zrips.CMILib.RawMessages.RawMessageCommand
 *  net.Zrips.CMILib.Time.CMITimeManager
 *  org.bukkit.command.CommandSender
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;

public class alert implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "inform",
        Arrays.asList("{gcp}!Alert!******** {gcs}[name] {gcw}just logged in! {gcp}*******!Alert!"));
    configReader.get("reason", "{gcp}Alert reason: {gcs}[reason]");
    configReader.get("removed", "{gcs}[playerDisplayName] {gcp}removed from alert list");
    configReader.get(
        "added",
        "{gcp}Alert message will be sent to all administrators, if {gcs}[playerDisplayName]"
            + " {gcp}join's in next 24Hours");
    configReader.get("online", "{gcs}[playerDisplayName] {gcp}already online");
    configReader.get("add", "{gcu}Add extra day");
    configReader.get(
        "list", " {gcs}[place]. {gcs}[playerDisplayName] {gcs}[time] {gcn}Reason: [reason]");
  }

  @Override
  @CAnnotation(
      priority = 70,
      info = "Alerts administration on players login",
      args = "[add/list/remove] [playerName] (reason) (-s)",
      tab = {"add%%list%%remove", "playerName"},
      explanation = {
        "{gcp}Permissions:",
        "{gcs}command.alert.info.inform {gcp}- to see alerts on player login"
      },
      others = false)
  public Boolean perform(final CMI cMI, CommandSender commandSender, final String[] stringArray) {
    Object object;
    Object targetName = null;
    UUID uUID = null;
    Action action = null;
    String string = "";
    boolean bl = false;
    String[] args = stringArray;
    int n = stringArray.length;
    int index = 0;
    while (index < n) {
      object = args[index];
      switch (((String) object).toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            if (uUID == null) {
              try {
                uUID = UUID.fromString((String) object);
                break;
              } catch (Throwable throwable) {
                // empty catch block
              }
            }
            if (action == null && (action = Action.getByName((String) object)) != null) break;
            if (targetName == null) {
              targetName = object;
              break;
            }
            if (!string.isEmpty()) {
              string = String.valueOf(string) + " ";
            }
            string = String.valueOf(string) + (String) object;
          }
      }
      ++index;
    }
    if (action == null) {
      action = Action.list;
    }
    switch (action) {
      case add:
        {
          object = cMI.getPlayerManager().getUser((String) targetName);
          if (object == null) {
            LC.info_NoPlayer.sendMessage((Object) commandSender, new Object[0]);
            return false;
          }
          if (string.isEmpty()) {
            string = null;
          }
          if (((CMIUser) object).getName().equalsIgnoreCase(commandSender.getName())) {
            return false;
          }
          if (((CMIUser) object).isOnline()) {
            if (!bl) {
              CMILC.info((Object) this, commandSender, "online", object);
            }
            return null;
          }
          ((CMIUser) object).setAlertUntil(System.currentTimeMillis() + 60000L * Config.alertTimer);
          ((CMIUser) object).setAlertReason(string);
          if (!bl) {
            CMILC.info((Object) this, commandSender, "added", object);
          }
          this.perform(cMI, commandSender, new String[] {"list", ((CMIUser) object).getName()});
          break;
        }
      case remove:
        {
          if (uUID == null && targetName == null) {
            return false;
          }
          object = cMI.getPlayerManager().getUser(uUID);
          if (object == null) {
            object = cMI.getPlayerManager().getUser((String) targetName);
          }
          index = 0;
          if (object != null) {
            index = ((CMIUser) object).getAlertUntil() > System.currentTimeMillis() ? 1 : 0;
            ((CMIUser) object).removeAlert();
          }
          if (bl) break;
          if (index != 0) {
            CMILC.info((Object) this, commandSender, "removed", object);
          }
          this.perform(cMI, commandSender, new String[] {"list"});
          break;
        }
      case list:
        {
          if (cMI.getPlayerManager().getAlerts().isEmpty()) {
            LC.info_nothingToShow.sendMessage((Object) commandSender, new Object[0]);
            return null;
          }
          n = 0;
          LC.info_Spliter.sendMessage((Object) commandSender, new Object[0]);
          Object targetUser = cMI.getPlayerManager().getUser((String) targetName);
          for (final UUID alertUuid : new HashSet<UUID>(cMI.getPlayerManager().getAlerts())) {
            if (targetUser != null && !((CMIUser) targetUser).getUniqueId().equals(alertUuid))
              continue;
            object = cMI.getPlayerManager().getUser(alertUuid);
            if (((CMIUser) object).getAlertUntil() == 0L
                || ((CMIUser) object).getAlertUntil() < System.currentTimeMillis()) {
              cMI.getPlayerManager().removeAlert(alertUuid);
              continue;
            }
            ++n;
            Snd snd = new Snd();
            snd.setTarget((CMIUser) object);
            snd.setSender(commandSender);
            RawMessage rawMessage = new RawMessage();
            rawMessage.addText(LC.modify_deleteSymbol.getLocale(new Object[0]));
            rawMessage.addHover(LC.modify_deleteSymbolHover.getLocale(new Object[] {"[text]", ""}));
            rawMessage.addCommand(
                String.valueOf(CommandsHandler.getLabel())
                    + " "
                    + this.getClass().getSimpleName()
                    + " "
                    + Action.remove.toString()
                    + " "
                    + alertUuid);
            rawMessage.addText(" " + LC.modify_addSymbol.getLocale(new Object[0]));
            rawMessage.addHover(CMILC.getIM(this, "add", new Object[0]));
            RawMessageCommand rawMessageCommand =
                new RawMessageCommand() {

                  public void run(CommandSender commandSender) {
                    CMIUser cMIUser = CMIUser.getUser(alertUuid);
                    cMIUser.setAlertUntil(
                        cMIUser.getAlertUntil() + 86400000L, cMIUser.getAlertReason());
                    alert.this.perform(cMI, commandSender, stringArray);
                  }
                };
            rawMessage.addCommand(rawMessageCommand);
            rawMessage.addText(
                CMILC.getIM(
                    this,
                    "list",
                    "[place]",
                    n,
                    "[reason]",
                    ((CMIUser) object).getAlertReason() == null
                        ? "N/A"
                        : ((CMIUser) object).getAlertReason(),
                    "[time]",
                    CMITimeManager.toOnlyHoursShort(
                        (Long) (((CMIUser) object).getAlertUntil() - System.currentTimeMillis()),
                        (boolean) true,
                        (boolean) true),
                    snd));
            rawMessage.show(commandSender);
          }
          if (n != 0) break;
          LC.info_nothingToShow.sendMessage((Object) commandSender, new Object[0]);
          break;
        }
    }
    return true;
  }

  private static enum Action {
    add,
    list,
    remove;

    public static Action getByName(String string) {
      Action[] actionArray = Action.values();
      int n = actionArray.length;
      int index = 0;
      while (index < n) {
        Action action = actionArray[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
        ++index;
      }
      return null;
    }
  }
}
