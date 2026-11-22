package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.Placeholders.Placeholder;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class placeholders implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("list", "{gcs}[place]{gcp}. {gcs}[placeholder]");
    configReader.get("outputResult", " {gcp}result: {gcs}[result]");
    configReader.get(
        "parse", "{gcs}[placeholder] {gcp}by {gcs}[source] {gcp}result &8|&r[result]&8|");
  }

  @CAnnotation(
      info = "List out all placeholders",
      args = "(parse) (placeholder) (playerName)",
      tab = {"%%parse", "placeholders", "playername"},
      regVar = {0, 1, 2, 3},
      consoleVar = {0, 2, 3},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    Player player = null;
    if (commandSender instanceof Player) {
      player = (Player) commandSender;
    }

    int value = 1;
    if (strings.length > 0 && strings[0].startsWith("-p:")) {
      try {
        value = Integer.parseInt(strings[0].substring("-p:".length(), strings[0].length()));
      } catch (Exception exception) {
      }
    }

    if (strings.length >= 2 && strings[0].equalsIgnoreCase("parse")) {
      String string = null;
      String text = null;
      String[] parts = strings;
      int count = strings.length;

      for (int countSecond = 0; countSecond < count; countSecond++) {
        String textSecond = parts[countSecond];
        if (!textSecond.equalsIgnoreCase("parse")) {
          if (!textSecond.contains("%") && !textSecond.contains("{")) {
            string = textSecond;
          } else {
            text = textSecond;
          }
        }
      }

      CMIUser cmiUser = cmi.getUser(commandSender, string, this, false);
      player = cmiUser == null ? player : cmiUser.getPlayer(false);
      Placeholder.CMIPlaceholderType cmiPlaceholderType =
          cmi.getPlaceholderAPIManager().getPlaceHolderType(player, text);
      CMILC.info(
          this,
          commandSender,
          "parse",
          "[placeholder]",
          text == null ? "" : "%." + text.substring(1, text.length() - 1) + ".%",
          "[source]",
          cmiPlaceholderType == null ? "Unknown" : cmiPlaceholderType.name(),
          "[result]",
          cmiPlaceholderType == Placeholder.CMIPlaceholderType.CMI
              ? cmi.getPlaceholderAPIManager()
                  .translateOwnPlaceHolder(cmiUser != null ? cmiUser.getUniqueId() : null, text)
              : cmi.getPlaceholderAPIManager().updatePlaceHolders(player, text));
      return true;
    } else {
      PageInfo pageInfo =
          new PageInfo(
              player != null ? 10 : Placeholder.CMIPlaceHolders.getCount(),
              Placeholder.CMIPlaceHolders.getCount(),
              value);
      Placeholder.CMIPlaceHolders[] cmiPlaceHolderses;
      int countThird = (cmiPlaceHolderses = Placeholder.CMIPlaceHolders.values()).length;

      for (int countFourth = 0; countFourth < countThird; countFourth++) {
        Placeholder.CMIPlaceHolders cmiPlaceHolders = cmiPlaceHolderses[countFourth];
        if (!cmiPlaceHolders.isHidden()) {
          if (pageInfo.isBreak()) {
            break;
          }

          if (pageInfo.isEntryOk()) {
            RawMessage rawMessage = new RawMessage();
            String textThird = "";
            if (player != null && !cmiPlaceHolders.isComplex()) {
              String textFourth =
                  cmi.getPlaceholderAPIManager()
                      .updatePlaceHolders(player, cmiPlaceHolders.getFull());
              textThird =
                  cmi.getPlaceholderAPIManager()
                      .updatePlaceHolders(
                          player, CMILC.getIM(this, "outputResult", "[result]", textFourth));
            }

            String textFifth = cmiPlaceHolders.getFull();
            StringBuilder stringBuilder = new StringBuilder();
            if (CMIHookType.PlaceholderAPI.isEnabled()) {
              textFifth = cmiPlaceHolders.getFull();
              stringBuilder.append(cmiPlaceHolders.getFull());
            }

            if (CMIHookType.MVdWPlaceholderAPI.isEnabled()) {
              if (!CMIHookType.PlaceholderAPI.isEnabled()) {
                textFifth =
                    cmiPlaceHolders.getFull().substring(1, cmiPlaceHolders.getFull().length() - 2);
                textFifth = "{" + textFifth + "}";
              }

              if (stringBuilder.toString().isEmpty()) {
                stringBuilder.append(cmiPlaceHolders.getFull());
              } else {
                stringBuilder.append(
                    "\n{"
                        + cmiPlaceHolders
                            .getFull()
                            .substring(1, cmiPlaceHolders.getFull().length() - 2)
                        + "}");
              }
            }

            if (cmiPlaceHolders.getDescription() != null) {
              if (!stringBuilder.toString().isEmpty()) {
                stringBuilder.append("\n");
              }

              stringBuilder.append(cmiPlaceHolders.getDescription());
            }

            rawMessage
                .addText(
                    CMILC.getIM(
                            this,
                            "list",
                            "[place]",
                            pageInfo.getPositionForOutput(),
                            "[placeholder]",
                            textFifth)
                        + textThird)
                .addHover(stringBuilder.toString())
                .addSuggestion(cmiPlaceHolders.getFull());
            rawMessage.show(commandSender);
          }
        }
      }

      if (player != null) {
        pageInfo.autoPagination(
            commandSender,
            CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(),
            "-p:");
      }

      return true;
    }
  }
}
