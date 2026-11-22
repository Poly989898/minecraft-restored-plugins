package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.ListEditor;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class editlocale implements Cmd {
  private static CMITask task = null;

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("found", "{gcs}********* {gcp}Found: [found] {gcs}*********");
  }

  @CAnnotation(
      info = "Edit your locale file",
      args = "(keyword(-s))",
      regVar = -100,
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String key = null;
    Integer integer = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (!arg.startsWith("-")) {
        if (string == null) {
          string = "";
        }

        if (!string.isEmpty()) {
          string = string + " ";
        }

        string = string + arg;
      } else {
        if (arg.startsWith("-c:")) {
          key = arg.substring("-c:".length(), arg.length());
        }

        if (arg.startsWith("-p:")) {
          try {
            integer = Integer.parseInt(arg.substring("-p:".length(), arg.length()));
          } catch (Exception exception) {
          }
        }
      }
    }

    if (integer == null) {
      integer = 1;
    }

    if (string != null && key == null) {
      try {
        ConfigReader configReader = cmi.getConfigManager().getLocaleConfig();
        var map = configReader.getC().getValues(true);
        HashMap<String, Object> hashMap = new HashMap();
        String normalized = CMIChatColor.stripColor(string).toLowerCase().replace(" ", "");

        for (Entry entry : map.entrySet()) {
          if (entry.getValue() instanceof String) {
            String current = (String) entry.getValue();
            if (current.toLowerCase().replace(" ", "").contains(normalized)) {
              hashMap.put((String) entry.getKey(), entry.getValue());
            }
          } else if (entry.getValue() instanceof List) {
            for (Object object : (List<Object>) entry.getValue()) {
              if (object.toString().toLowerCase().replace(" ", "").contains(normalized)) {
                hashMap.put((String) entry.getKey(), entry.getValue());
                break;
              }
            }
          }

          if (((String) entry.getKey()).toLowerCase().replace(" ", "").contains(normalized)) {
            hashMap.put((String) entry.getKey(), entry.getValue());
          }
        }

        CMILC.info(this, commandSender, "found", "[found]", hashMap.size());
        PageInfo pageInfo = new PageInfo(10, hashMap.size(), integer);
        int matchCount = 0;

        for (Entry localeEntry : hashMap.entrySet()) {
          if (!pageInfo.isContinue()) {
            if (pageInfo.isBreak()) {
              break;
            }

            matchCount++;
            RawMessage rawMessage = new RawMessage();
            rawMessage.addText(
                "&7"
                    + pageInfo.getPositionForOutput()
                    + (matchCount % 2 == 1 ? ". &e" : ". &6")
                    + (String) localeEntry.getKey());
            if (localeEntry.getValue() instanceof String) {
              rawMessage.addHoverText((String) localeEntry.getValue());
            } else if (localeEntry.getValue() instanceof List) {
              List list = (List) localeEntry.getValue();
              rawMessage.addHoverText(list);
            }

            rawMessage.addCommand(
                CommandsHandler.getLabel()
                    + " "
                    + this.getClass().getSimpleName()
                    + " -c:"
                    + (String) localeEntry.getKey());
            rawMessage.show(commandSender);
          }
        }

        pageInfo.autoPagination(
            commandSender,
            CommandsHandler.getLabel() + " " + this.getClass().getSimpleName() + " " + string,
            "-p:");
      } catch (Exception exception2) {
        exception2.printStackTrace();
      }

      return true;
    } else if (key != null) {
      ConfigReader localeConfig = cmi.getConfigManager().getLocaleConfig();
      localeConfig.load();
      Object localeValue = localeConfig.getC().getValues(true).get(key);
      if (string == null) {
        if (localeValue instanceof String) {
          RawMessage message = new RawMessage();
          message.addText(LC.modify_commandEditInfoHover.getLocale(new Object[0]));
          message.addHover(LC.info_Click.getLocale(new Object[0]));
          message.addSuggestion((String) localeValue);
          message.show(commandSender);
          cmi.getCTextManager()
              .addChatEditor(
                  (Player) commandSender,
                  CommandsHandler.getLabel()
                      + " "
                      + this.getClass().getSimpleName()
                      + " -c:"
                      + key);
        } else if (localeValue instanceof List) {
          List lines = (List) localeValue;
          ListEditor.showListForEditing(
              (Player) commandSender,
              this.getClass().getSimpleName() + " -c:" + key,
              "",
              lines,
              true);
        }
      } else if (localeValue instanceof String) {
        localeConfig.set(key, string);
        localeConfig.getContents().put(key, string);
        localeConfig.save();
        CMIMessages.sendMessage(commandSender, "&7Example: &r" + string);
        cmi.getLM().getCustom().set(key, string);
        save();
        cmi.getLM().reload();
      } else if (localeValue instanceof List) {
        List linesCopy = (List) localeValue;
        ListEditor.processEditListClean(
            strings,
            (Player) commandSender,
            this.getClass().getSimpleName() + " -c:" + key,
            "",
            linesCopy,
            true,
            null);
        localeConfig.set(key, linesCopy);
        localeConfig.getContents().put(key, linesCopy);
        cmi.getLM().getCustom().set(key, linesCopy);
        save();
        cmi.getLM().reload();
      }

      return true;
    } else {
      return true;
    }
  }

  private static void save() {
    if (task == null) {
      task =
          CMIScheduler.runTaskLater(
              CMI.getInstance(),
              () -> {
                CMI.getInstance().getConfigManager().getLocaleConfig().save();
                task = null;
              },
              1200L);
    }
  }
}
