package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Containers.CommandAliasType;
import com.Zrips.CMI.Containers.PInfo;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.CMICommand;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.HashMap;
import java.util.Map.Entry;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;

public class checkcommand implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcs}[place]{gcp}. {gcs}[command] {gcn}[args]");
    configReader.get("hover", "{gcs}[desc]");
    configReader.get("variableColor", "{gcn}");
    configReader.get("noCommandFound", "{gcw}No commands found by this keyword");
  }

  @CAnnotation(
      info = "Search for possible commands by keyword",
      args = "(key word)",
      regVar = {0, 1, 2},
      consoleVar = {0, 1, 2},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Integer integer = null;
    String string = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      if (text.startsWith("-p:")) {
        try {
          integer = Integer.parseInt(text.replace("-p:", ""));
          continue;
        } catch (Exception exception) {
        }
      }

      string = text.toLowerCase();
    }

    if (integer == null) {
      integer = 1;
    }

    HashMap<CMICommand, PInfo> hashMap = new HashMap();
    if (string != null) {
      for (var entry : cmi.getCommandManager().getCommands().entrySet()) {
        CMICommand cmiCommand = (CMICommand) entry.getValue();
        if (!cmiCommand.getAnottation().hidden()) {
          if (cmiCommand.getName().contains(string)
              || cmiCommand.getAnottation().args().toLowerCase().contains(string)
              || cmiCommand.getAnottation().info().toLowerCase().contains(string)) {
            hashMap.put(
                cmiCommand, new PInfo(cmiCommand.getName(), cmiCommand.getAnottation().info()));
          } else if (cmiCommand.getAnottation().explanation() != null
              && cmiCommand.getAnottation().explanation().length > 0) {
            String[] partsSecond;
            int countSecond = (partsSecond = cmiCommand.getAnottation().explanation()).length;

            for (int countThird = 0; countThird < countSecond; countThird++) {
              String textSecond = partsSecond[countThird];
              if (textSecond.toLowerCase().contains(string)) {
                hashMap.put(
                    cmiCommand, new PInfo(cmiCommand.getName(), cmiCommand.getAnottation().info()));
              }
            }
          }
        }
      }

      for (var varSecond : cmi.getAliasManager().getDefaultAlias().entrySet()) {
        CommandAlias commandAlias = (CommandAlias) varSecond.getValue();
        if (commandAlias.getType() == CommandAliasType.subbase) {
          CMICommand cmiCommandValue =
              cmi.getCommandManager().getCommands().get(commandAlias.getCmiCommandName());
          if (cmiCommandValue != null && commandAlias.getAlias().contains(string)) {
            hashMap.put(
                cmiCommandValue,
                new PInfo(commandAlias.getAlias(), cmiCommandValue.getAnottation().info()));
          }
        }
      }
    } else {
      for (var varThird : cmi.getCommandManager().getCommands().entrySet()) {
        CMICommand cmiCommandSecond = (CMICommand) varThird.getValue();
        if (!cmiCommandSecond.getAnottation().hidden()) {
          hashMap.put(
              cmiCommandSecond,
              new PInfo(cmiCommandSecond.getName(), cmiCommandSecond.getAnottation().info()));
        }
      }
    }

    if (hashMap.isEmpty()) {
      CMILC.info(this, commandSender, "noCommandFound");
      return null;
    }

    PageInfo pageInfo = new PageInfo(10, hashMap.size(), integer);
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);

    for (Entry entrySecond : hashMap.entrySet()) {
      if (pageInfo.isEntryOk()) {
        if (pageInfo.isBreak()) {
          break;
        }

        RawMessage rawMessage = new RawMessage();
        rawMessage
            .addText(
                CMILC.getIM(
                    this,
                    "list",
                    "[place]",
                    pageInfo.getPositionForOutput(),
                    "[command]",
                    ((CMICommand) entrySecond.getKey()).getName(),
                    "[args]",
                    ((CMICommand) entrySecond.getKey()).getTranslatedArgs()))
            .addHover(
                CMILC.getIM(
                    this,
                    "hover",
                    "[desc]",
                    cmi.getLM()
                        .getMessage(
                            "command."
                                + ((CMICommand) entrySecond.getKey()).getName()
                                + ".help.info")))
            .addCommand(
                CMIChatColor.stripColor(
                    "/"
                        + CommandsHandler.getLabel()
                        + " "
                        + ((CMICommand) entrySecond.getKey()).getName()
                        + " ?"))
            .addSuggestion(
                "/"
                    + CommandsHandler.getLabel()
                    + " "
                    + ((CMICommand) entrySecond.getKey()).getName());
        rawMessage.show(commandSender);
      }
    }

    pageInfo.autoPagination(
        commandSender,
        CommandsHandler.getLabel()
            + " "
            + this.getClass().getSimpleName()
            + (string == null ? "" : " " + string),
        "-p:");
    return true;
  }
}
