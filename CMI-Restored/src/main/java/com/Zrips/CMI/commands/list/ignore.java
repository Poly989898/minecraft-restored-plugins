package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.UUID;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ignore implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("self", "{gcw}Don't be silly, you can't ignore yourself");
    configReader.get("added", "{gcp}You're now ignoring {gcs}[playerDisplayName]");
    configReader.get("addedAll", "{gcp}You're now ignoring everyone");
    configReader.get("all", "{gcp}Everyone");
    configReader.get("removed", "{gcp}You're no longer ignoring {gcs}[playerDisplayName]");
    configReader.get("removedAll", "{gcp}You're no longer ignoring everyone");
    configReader.get("list", "{gcs}[place]{gcp}. {gcs}[playerDisplayName]  ");
    configReader.get("ignoringYou", "{gcs}[playerDisplayName] {gcp}is ignoring you");
    configReader.get("empty", "{gcp}Ignore list is empty");
  }

  @CAnnotation(
      priority = 48,
      info = "Ignores player",
      args = "(playerName/uuid/all) (-p:[playerName])",
      tab = "playername%%all",
      explanation = "cmi.command.ignore.bypass - to bypass ignore list",
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      customAlias = "!ignorelist",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Integer integer = null;
    String string = null;
    String text = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String textSecond = parts[count];
      if (string == null && cmi.getPlayerManager().getUser(textSecond) != null) {
        string = textSecond;
      } else {
        try {
          integer = Integer.parseInt(textSecond);
        } catch (Exception exception) {
          if (textSecond.startsWith("-p:")) {
            text = textSecond.substring("-p:".length());
          } else {
            string = textSecond;
          }
        }
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, text, this);
    if (cmiUser == null) {
      return null;
    }

    if (string != null && integer == null) {
      UUID uuid = null;
      if (string != null
          && (string.equalsIgnoreCase("all")
              || string.equalsIgnoreCase(cmi.getPlayerManager().getEmptyUserUUID().toString()))) {
        uuid = cmi.getPlayerManager().getEmptyUserUUID();
        Snd snd = new Snd().setSender(commandSender).setTarget(Bukkit.getConsoleSender());
        if (cmiUser.isIgnoring(uuid)) {
          cmiUser.removeIgnore(uuid);
          CMILC.info(this, commandSender, "removedAll", snd);
        } else {
          cmiUser.addIgnore(uuid, true);
          CMILC.info(this, commandSender, "addedAll", snd);
        }
      } else {
        CMIUser cMIUserSecond = cmi.getUser(commandSender, string, this, false);
        if (cMIUserSecond == null) {
          return null;
        }

        if (cmiUser.getUniqueId().equals(cMIUserSecond.getUniqueId())) {
          CMILC.info(this, commandSender, "self");
          return null;
        }

        uuid = cMIUserSecond.getUniqueId();
        Snd sndSecond = new Snd().setSender(commandSender).setTarget(cMIUserSecond);
        if (cmiUser.isIgnoring(uuid)) {
          cmiUser.removeIgnore(uuid);
          CMILC.info(this, commandSender, "removed", sndSecond);
        } else {
          cmiUser.addIgnore(uuid, true);
          CMILC.info(this, commandSender, "added", sndSecond);
        }
      }

      cmiUser.addForDelayedSave();
      return true;
    } else {
      if (integer == null) {
        integer = 1;
      }

      var set = cmiUser.getIgnores();
      if (set.isEmpty()) {
        CMILC.info(this, commandSender, "empty");
        return null;
      }

      PageInfo pageInfo = new PageInfo(15, set.size(), integer);

      for (UUID uUIDSecond : set) {
        if (pageInfo.isEntryOk()) {
          RawMessage rawMessage = new RawMessage();
          Snd sndThird = null;
          if (uUIDSecond.equals(cmi.getPlayerManager().getEmptyUserUUID())) {
            sndThird = new Snd().setSender(commandSender).setTarget(Bukkit.getConsoleSender());
          } else {
            CMIUser cMIUserThird = cmi.getPlayerManager().getUser(uUIDSecond);
            sndThird = new Snd().setSender(commandSender).setTarget(cMIUserThird);
          }

          String textThird =
              CMILC.getIM(this, "list", "[place]", pageInfo.getPositionForOutput(), sndThird);
          if (uUIDSecond.equals(cmi.getPlayerManager().getEmptyUserUUID())) {
            textThird = textThird.replace("[playerDisplayName]", CMILC.getIM(this, "all"));
          }

          rawMessage.addText(textThird);
          rawMessage
              .addText(LC.modify_deleteSymbol)
              .addHover(
                  LC.modify_deleteSymbolHover.getLocale(
                      new Object[] {
                        "[text]",
                        sndThird.getTargetUser() == null
                            ? uUIDSecond
                            : sndThird.getTargetUser().getDisplayName()
                      }))
              .addCommand(
                  "cmi ignore " + uUIDSecond + (text != null ? " -p:" + cmiUser.getName() : ""));
          rawMessage.show(commandSender);
        }
      }

      pageInfo.autoPagination(
          commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName());
      return true;
    }
  }
}
