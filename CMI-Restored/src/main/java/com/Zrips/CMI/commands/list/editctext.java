package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CustomText.CText;
import com.Zrips.CMI.Modules.CustomText.CTextPage;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class editctext implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("noCText", "{gcw}Cant find custom text by this name ([name])");
    configReader.get(
        "editInfo",
        "{gcp}Click to paste old text. Type {gcs}cancel {gcp}to cancel action. Type {gcs}remove"
            + " {gcp}to remove line");
    configReader.get("addInfo", "{gcp}Enter new line text. Type {gcs}cancel {gcp}to cancel");
    configReader.get(
        "addInformationHover",
        "{gcp}Global variables supported as of sender category \n"
            + "{gcp}To include clickable messages: \n"
            + "{gcp}<T>&rText{gcp}</T><H>&rHover text{gcp}</H><C>&rcommand{gcp}</C><SC>&rSuggested"
            + " text{gcp}</SC> \n"
            + "{gcs}<T> {gcp}and {gcs}</T> {gcp}required, other is optional \n"
            + "{gcp}Use \\u005Cn to break line \n"
            + "{gcp}To have more than one JSON message use <Next> \n"
            + "{gcs}<SC> {gcp}Suggests text when clicked \n"
            + "{gcs}<SI> {gcp}Suggests text when shift clicked which doesnt everrite current text"
            + " in chat \n"
            + "{gcs}<CC> {gcp}performs console command once \n"
            + "{gcs}<CCI> {gcp}performs console commands every time player clicks text \n"
            + "{gcs}<URL> {gcp}allows to add url to redirect player into web page");
    configReader.get(
        "newInfo", "{gcp}Enter name of new custom text. Type {gcs}cancel {gcp}to cancel");
    configReader.get(
        "deleteInfo",
        "{gcp}Type in {gcs}confirm {gcp}to confirm removal or type {gcs}cancel {gcp}to cancel this"
            + " action");
    configReader.get("deleted", "{gcp}Custom text ({gcs}[ctext]{gcp}) was removed");
    configReader.get("createdNew", "{gcp}Custom text ({gcs}[ctext]{gcp}) was created");
    configReader.get("hidden", "{gcp}Hidden: {gcs}[state]");
    configReader.get("autoPage", "{gcp}Auto page: {gcs}[state]");
    configReader.get("autoAlias", "{gcp}Auto alias: {gcs}[state]");
    configReader.get("permReq", "{gcp}Requires permission: {gcs}[state]");
    configReader.get("pageLabel", " {gcp}<PageLabel>");
    configReader.get("pageLabelHover", "{gcp}[value]");
    configReader.get("listTitle", "{gcp}CText's");
    configReader.get("listFormat", "{gcs}[name]&7: &r[extra]");
  }

  @CAnnotation(
      info = "Custom text editor",
      regVar = -66,
      consoleVar = 666,
      others = false,
      multiTab = "[ctext]")
  @Override
  public Boolean perform(final CMI cmi, final CommandSender commandSender, String[] strings) {
    String string = null;
    Integer integer = 1;
    int lineIndex = 0;
    int newIndex = 0;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.startsWith("-p:")) {
        try {
          integer = Integer.parseInt(arg.substring("-p:".length()));
          continue;
        } catch (Exception exception) {
        }
      }

      if (string == null) {
        string = arg;
      }
    }

    if (string == null) {
      PageInfo pageInfo = new PageInfo(10, cmi.getCTextManager().getAll().size(), integer);
      LC.info_SpliterValue.sendMessage(
          commandSender, new Object[] {"[value]", CMILC.getIM(this, "listTitle")});
      var arrayList = new ArrayList<>(cmi.getCTextManager().getAll().keySet());
      ChatMessageObjectEdit chatMessageObjectEdit =
          new ChatMessageObjectEdit(commandSender, pageInfo) {
            public void newAdd(String text) {
              cmi.performCommand(commandSender, "cmi editctext new " + text);
            }
          };
      RawMessage rawMessage = new RawMessage();
      chatMessageObjectEdit.setTopLine(rawMessage);

      for (int index = 0; index < cmi.getCTextManager().getAll().size(); index++) {
        if (pageInfo.isEntryOk()) {
          final CText cText = cmi.getCTextManager().getAll().get(arrayList.get(index));
          String joined = "";

          for (CTextPage cTextPage : cText.getPagesMap().values()) {
            if (cTextPage != null && !cTextPage.getLines().isEmpty()) {
              joined = cTextPage.getLines().get(0);
              break;
            }
          }

          if (joined.length() > 32) {
            joined = joined.substring(0, 32) + "..";
          }

          joined = CMIChatColor.stripColor(joined);
          ChatEditorObject chatEditorObject =
              new ChatEditorObject(
                  CMILC.getIM(this, "listFormat", "[name]", cText.getName(), "[extra]", joined)) {
                public void onDelete() {
                  cmi.performCommand(commandSender, "cmi editctext " + cText.getName() + " delete");
                }

                public void onClick() {
                  CMI.getInstance()
                      .performCommand(commandSender, "cmi editctext " + cText.getName());
                }
              };
          chatEditorObject.setHover(
              LC.modify_editSymbolHover.getLocale(new Object[] {"[text]", cText.getName()}));
          chatMessageObjectEdit.addline(chatEditorObject);
        }
      }

      chatMessageObjectEdit.print();
      pageInfo.autoPagination(
          commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(), "-p:");
      return true;
    } else {
      Player player = (Player) commandSender;
      if (strings.length != 1 && strings.length != 2 || !strings[0].equalsIgnoreCase("new")) {
        CText cText = cmi.getCTextManager().getCText(string);
        if (cText == null) {
          CMILC.info(this, commandSender, "noCText", "[name]", string);
          return null;
        }

        if (strings.length > 1) {
          String displayText;
          switch ((displayText = strings[1]).hashCode()) {
            case -1335458389:
              if (displayText.equals("delete")) {
                cmi.getCTextManager().removeChatEditor(player);
                cmi.getCTextManager().deleteCText(cText.getName());
                cmi.getCTextManager().delete(cText);
                CMILC.info(this, player, "deleted", "[ctext]", cText.getName());
                return true;
              }
              break;
            case -1217487446:
              if (displayText.equals("hidden")) {
                Boolean hasChat = null;
                if (strings[2].equalsIgnoreCase("true")) {
                  hasChat = true;
                } else if (strings[2].equalsIgnoreCase("false")) {
                  hasChat = false;
                }

                if (hasChat != null) {
                  cText.setHidden(hasChat);
                  player.performCommand("cmi editctext " + cText.getName());
                  cmi.getCTextManager().save(cText);
                  return true;
                }

                return true;
              }
              break;
            case -678617180:
              if (displayText.equals("permreg")) {
                Boolean hasTitle = null;
                if (strings[2].equalsIgnoreCase("true")) {
                  hasTitle = true;
                }

                if (strings[2].equalsIgnoreCase("false")) {
                  hasTitle = false;
                }

                if (hasTitle != null) {
                  cText.setRequirePermission(hasTitle);
                  player.performCommand("cmi editctext " + cText.getName());
                  cmi.getCTextManager().save(cText);
                  return true;
                }

                return true;
              }
              break;
            case 1439551678:
              if (displayText.equals("autopage")) {
                Boolean hasHover = null;
                if (strings[2].equalsIgnoreCase("true")) {
                  hasHover = true;
                }

                if (strings[2].equalsIgnoreCase("false")) {
                  hasHover = false;
                }

                if (hasHover != null) {
                  cText.setAutoPage(hasHover);
                  player.performCommand("cmi editctext " + cText.getName());
                  cmi.getCTextManager().save(cText);
                  return true;
                }

                return true;
              }
              break;
            case 1662905857:
              if (displayText.equals("autoalias")) {
                Boolean hasClick = null;
                if (strings[2].equalsIgnoreCase("true")) {
                  hasClick = true;
                }

                if (strings[2].equalsIgnoreCase("false")) {
                  hasClick = false;
                }

                if (hasClick != null) {
                  CommandAlias commandAlias =
                      cmi.getAliasManager().getCustom().get(cText.getName().toLowerCase());
                  if (hasClick) {
                    if (commandAlias == null) {
                      commandAlias =
                          new CommandAlias(
                              cText.getName().toLowerCase(),
                              Arrays.asList("cmi ctext " + cText.getName()),
                              true);
                    } else {
                      commandAlias.setState(true);
                    }

                    cmi.getAliasManager().addCustom(commandAlias);
                  } else if (commandAlias != null) {
                    commandAlias.setState(false);
                    cmi.getAliasManager().addCustom(commandAlias);
                  }

                  cText.setAutoAlias(hasClick, true);
                  player.performCommand("cmi editctext " + cText.getName());
                  cmi.getCTextManager().save(cText);
                  return true;
                }

                return true;
              }
          }
        }

        if (strings.length == 1) {
          cmi.getCTextManager().showCTextEditor(commandSender, cText, 1);
          return true;
        }

        if (strings.length < 2) {
          return false;
        }

        if (strings.length > 2) {
          String replacement;
          switch ((replacement = strings[2]).hashCode()) {
            case -934610812:
              if (replacement.equals("remove")) {
                byte b = -1;

                try {
                  lineIndex = Integer.parseInt(strings[3]);
                } catch (Exception exception2) {
                  return false;
                }

                cmi.getCTextManager().removeCTextLine(cText, integer, lineIndex);
                cmi.getCTextManager().save(cText);
                player.performCommand("cmi editctext " + cText.getName() + " " + integer + " list");
                return true;
              }
              break;
            case 96417:
              if (replacement.equals("add")) {
                byte mode = -1;

                try {
                  lineIndex = Integer.parseInt(strings[3]);
                } catch (Exception exception3) {
                  return false;
                }

                if (strings.length == 4) {
                  cmi.getCTextManager()
                      .addChatEditor(
                          player,
                          "cmi editctext "
                              + cText.getName()
                              + " "
                              + integer
                              + " add "
                              + lineIndex
                              + " ");
                  RawMessage preview = new RawMessage();
                  preview
                      .addText(CMILC.getIM(this, "addInfo"))
                      .addHover(CMILC.getIM(this, "addInformationHover"));
                  preview.show(commandSender);
                  return true;
                }

                if (strings[4].equalsIgnoreCase("cancel")) {
                  cmi.getCTextManager().removeChatEditor(player);
                  player.performCommand(
                      "cmi editctext " + cText.getName() + " " + integer + " list");
                  return true;
                }

                String lineText = "";

                for (int argIndex = 4; argIndex < strings.length; argIndex++) {
                  if (!lineText.isEmpty()) {
                    lineText = lineText + " ";
                  }

                  lineText = lineText + strings[argIndex];
                }

                cText.addLine(integer.intValue(), lineText);
                cmi.getCTextManager().save(cText);
                player.performCommand("cmi editctext " + cText.getName() + " " + integer + " list");
                return true;
              }
              break;
            case 3108362:
              if (replacement.equals("edit")) {
                byte modeCopy = -1;

                try {
                  lineIndex = Integer.parseInt(strings[3]);
                } catch (Exception exception4) {
                  return false;
                }

                if (strings.length == 4) {
                  cmi.getCTextManager()
                      .addChatEditor(
                          player,
                          "cmi editctext "
                              + cText.getName()
                              + " "
                              + integer
                              + " edit "
                              + lineIndex
                              + " ");
                  RawMessage previewAlt = new RawMessage();
                  previewAlt
                      .addText(CMILC.getIM(this, "editInfo"))
                      .addHover(LC.info_PasteOld.getLocale(new Object[0]))
                      .addSuggestion(cText.getPageInfo(integer).getLines().get(lineIndex));
                  previewAlt.show(commandSender);
                  return true;
                }

                if (strings.length >= 4 && strings[4].equalsIgnoreCase("cancel")) {
                  cmi.getCTextManager().removeChatEditor(player);
                  player.performCommand(
                      "cmi editctext " + cText.getName() + " " + integer + " list");
                  return true;
                }

                if (strings.length >= 4 && strings[4].equalsIgnoreCase("remove")) {
                  cmi.getCTextManager().removeChatEditor(player);
                  cText.removeLine(integer.intValue(), lineIndex);
                  player.performCommand(
                      "cmi editctext " + cText.getName() + " " + integer + " list");
                  cmi.getCTextManager().save(cText);
                  return true;
                }

                String secondLine = "";

                for (int secondIndex = 4; secondIndex < strings.length; secondIndex++) {
                  if (!secondLine.isEmpty()) {
                    secondLine = secondLine + " ";
                  }

                  secondLine = secondLine + strings[secondIndex];
                }

                cText.replaceLine(integer.intValue(), lineIndex, secondLine);
                cmi.getCTextManager().save(cText);
                player.performCommand("cmi editctext " + cText.getName() + " " + integer + " list");
                return true;
              }
              break;
            case 3322014:
              if (replacement.equals("list")) {
                cmi.getCTextManager().showCTextEditor(commandSender, cText, integer);
                return true;
              }
              break;
            case 3357649:
              if (replacement.equals("move")) {
                byte styleCode = 0;
                if (strings[3].equalsIgnoreCase("up")) {
                  styleCode = -1;
                } else if (strings[3].equalsIgnoreCase("down")) {
                  styleCode = 1;
                }

                byte styleEnd = -1;

                try {
                  newIndex = Integer.parseInt(strings[4]);
                } catch (Exception exception5) {
                  return false;
                }

                cmi.getCTextManager()
                    .move(cText.getPageInfo(integer).getLines(), newIndex, styleCode);
                cmi.getCTextManager().save(cText);
                player.performCommand("cmi editctext " + cText.getName() + " " + integer + " list");
                return true;
              }
              break;
            case 1099397395:
              if (replacement.equals("removepage")) {
                cText.removePage(integer.intValue());
                cmi.getCTextManager().save(cText);
                player.performCommand(
                    "cmi editctext " + cText.getName() + " " + (integer - 1) + " list");
                return true;
              }
              break;
            case 1846033455:
              if (replacement.equals("newpage")) {
                cText.addPage(cText.getTotalPages() + 1, new CTextPage());
                player.performCommand(
                    "cmi editctext " + cText.getName() + " " + (integer + 1) + " list");
                return true;
              }
          }
        }

        cmi.getCTextManager().showCTextEditor(commandSender, cText, integer);
        return true;
      } else if (strings.length == 1) {
        cmi.getCTextManager().addChatEditor(player, "cmi editctext new ");
        CMILC.info(this, player, "newInfo");
        return true;
      } else if (strings[1].equalsIgnoreCase("cancel")) {
        cmi.getCTextManager().removeChatEditor(player);
        player.performCommand("cmi editctext");
        return true;
      } else {
        String name = strings[1];
        CText newText = cmi.getCTextManager().addNewCText(name);
        newText.setAutoAlias(true, true);
        cmi.getCTextManager().save(newText);
        CMILC.info(this, player, "createdNew", "[ctext]", newText.getName());
        player.performCommand("cmi editctext " + newText.getName());
        return true;
      }
    }
  }
}
