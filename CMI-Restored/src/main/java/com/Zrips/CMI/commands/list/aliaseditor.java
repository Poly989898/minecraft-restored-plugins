package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class aliaseditor implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("ListNumber", "{gcs}[number].");
    configReader.get("alias", "{gcs}[alias]");
    configReader.get("commandList", " {gcs}/[command]  ");
    configReader.get("commandListDelay", " {gcs}[command]  ");
    configReader.get("permRequirements", "Permission");
    configReader.get(
        "permReqExplanation", "{gcp}Will require {gcs}cmi.customalias.[alias] {gcp}to use it");
    configReader.get("tab", " TabComplete");
    configReader.get("tabCompleteExplanation", "{gcp}When disabled tabcomplete will not work");
    configReader.get("tabCompletes", "{gcu}TabCompletes: ");
    configReader.get("cTab", " CustomTab");
    configReader.get("file", " {gcu}FileName");
    configReader.get("fileExplanation", "{gcp}Current destination file: {gcs}[file]");
    configReader.get("addTabComplete", " AddTab");
    configReader.get(
        "addTabExplanation", "{gcp}When enabled we will add defined tabs to existing ones");
    configReader.get("exact", " Exact");
    configReader.get(
        "addExactExplanation",
        "{gcp}When enabled we will only use this alias when exact command is provided");
    configReader.get("newAliasInfo", "{gcp}Enter new alias command name and press enter");
    configReader.get("addInfo", "{gcp}Enter new command. Type {gcs}cancel {gcp}to cancel");
    configReader.get(
        "addInformationHover",
        "{gcp}Global variables supported as of sender category \n"
            + "{gcp}delay! 5 {gcs}to include delay in commands \n"
            + "{gcp}$1 {gcs}will take provided variable by new command and will place in that place"
            + " \n"
            + "{gcp}$1- {gcs}takes all variables from this point and inserts into original commands"
            + " \n"
            + "{gcp}? {gcs}makes it valid when using any last variable. For Help page usage\n"
            + "{gcp}fromConsole! will perform command from console");
    configReader.get(
        "editInfo",
        "{gcp}Click to paste old text. Type {gcs}cancel {gcp}to cancel action. Type {gcs}remove"
            + " {gcp}to remove line");
  }

  @CAnnotation(
      info = "Alias editor",
      args = "(new) (alias-cmd)",
      multiTab = "new,[customalias]",
      regVar = -66,
      consoleVar = 666)
  @Override
  public Boolean perform(final CMI cmi, final CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    var linkedHashMap = cmi.getAliasManager().getCustom();
    CommandAlias commandAlias = null;
    Integer integer = null;
    if (strings.length == 1) {
      try {
        integer = Integer.parseInt(strings[0]);
      } catch (Exception exception) {
      }

      String string;
      switch ((string = strings[0]).hashCode()) {
        case 96417:
          if (string.equals("add")) {
            cmi.getCTextManager().addChatEditor(player, "cmi aliaseditor add ");
            RawMessage rawMessage = new RawMessage();
            rawMessage.addText(CMILC.getIM(this, "newAliasInfo"));
            rawMessage.show(commandSender);
          }
      }
    }

    if (strings.length >= 2 && strings[0].equalsIgnoreCase("add")) {
      String text = "";

      for (int value = 1; value < strings.length; value++) {
        if (!text.isEmpty()) {
          text = text + " ";
        }

        text = text + strings[value];
      }

      commandAlias = new CommandAlias(text, new ArrayList<>(), true);
      cmi.getAliasManager().addCustom(commandAlias);
      cmi.performCommand(
          commandSender, "cmi aliaseditor " + commandAlias.getAliasAsOneWord() + " commands");
      cmi.getAliasManager().saveNew();
      return true;
    } else if (strings.length >= 2
        && strings[0].equalsIgnoreCase("new")
        && !strings[1].equalsIgnoreCase("commands")
        && !strings[1].equalsIgnoreCase("remove")) {
      String raw = "";

      for (int index = 1; index < strings.length; index++) {
        if (!raw.isEmpty()) {
          raw = raw + " ";
        }

        raw = raw + strings[index];
      }

      if (!raw.contains("-")) {
        return false;
      }

      String aliasName = raw.split("-")[0];
      String aliasArgs = raw.substring(aliasName.length() + 1, raw.length());
      commandAlias = new CommandAlias(aliasName, Arrays.asList(aliasArgs), true);
      cmi.getAliasManager().addCustom(commandAlias);
      cmi.performCommand(
          commandSender, "cmi aliaseditor " + commandAlias.getAliasAsOneWord() + " commands");
      cmi.getAliasManager().saveNew();
      return true;
    } else {
      if (strings.length > 0) {
        commandAlias =
            CMI.getInstance().getAliasManager().getAliasForCommand(strings[0].replace("__", " "));
      }

      if (strings.length == 1 && commandAlias == null) {
        CommandAlias existingAlias =
            CMI.getInstance().getAliasManager().getAliasForCommand(strings[0].replace("__", " "));
        if (existingAlias != null) {
          commandAlias = existingAlias;
        }
      }

      if (strings.length == 0 || strings.length == 1 && integer != null) {
        if (integer == null) {
          integer = 1;
        }

        PageInfo pageInfo = new PageInfo(10, linkedHashMap.size(), integer);
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        ArrayList arrayList = new ArrayList(linkedHashMap.keySet());
        ChatMessageObjectEdit chatMessageObjectEdit =
            new ChatMessageObjectEdit(commandSender, pageInfo) {
              public void newAdd(String newAlias) {
                CommandAlias newCommandAlias = new CommandAlias(newAlias, new ArrayList<>(), true);
                cmi.getAliasManager().addCustom(newCommandAlias);
                cmi.performCommand(
                    commandSender,
                    "cmi aliaseditor " + newCommandAlias.getAliasAsOneWord() + " commands");
                cmi.getAliasManager().saveNew();
              }
            };

        for (int mapIndex = linkedHashMap.size() - 1; mapIndex >= 0; mapIndex--) {
          if (pageInfo.isEntryOk()) {
            final CommandAlias mapAlias = (CommandAlias) linkedHashMap.get(arrayList.get(mapIndex));
            ChatEditorObject chatEditorObject =
                new ChatEditorObject(
                    CMILC.getIM(this, "alias", "[alias]", arrayList.get(mapIndex))) {
                  public void onDelete() {
                    cmi.getAliasManager().removeCustom(mapAlias);
                    cmi.performCommand(commandSender, "cmi aliaseditor");
                    cmi.getAliasManager().saveNew();
                  }

                  public void onClick() {
                    CMI.getInstance()
                        .performCommand(
                            commandSender,
                            "cmi aliaseditor " + mapAlias.getAliasAsOneWord() + " commands");
                  }
                };
            chatEditorObject.setHover(mapAlias.getCommandsForLore());
            chatMessageObjectEdit.addline(chatEditorObject);
          }
        }

        chatMessageObjectEdit.print();
        pageInfo.autoPagination(
            commandSender, CommandsHandler.getLabel() + " " + aliaseditor.class.getSimpleName());
        return true;
      } else {
        if (commandAlias != null) {
          if (strings.length == 1) {
            strings = Arrays.copyOf(strings, strings.length + 1);
            strings[strings.length - 1] = "commands";
          }

          final CommandAlias aliasForCommands = commandAlias;
          if (strings.length > 1) {
            String section;
            switch ((section = strings[1]).hashCode()) {
              case -602535288:
                if (section.equals("commands")) {
                  ChatMessageListEdit chatMessageListEdit =
                      new ChatMessageListEdit(
                          player, commandAlias.getCommands(), ChatEditType.String) {
                        public void onUpdate() {
                          cmi.getAliasManager().saveNew();
                          aliasForCommands.recalculateDynamicVariables();
                        }

                        public void beforePrint() {
                          RawMessage commandsMessage = new RawMessage();
                          RawMessageCommand rawMessageCommand =
                              new RawMessageCommand() {
                                public void run(CommandSender clickSender) {
                                  aliasForCommands.setRequiresPerm(
                                      !aliasForCommands.isRequiresPerm());
                                  print();
                                  cmi.getAliasManager().saveNew();
                                }
                              };
                          commandsMessage.addText(
                              LC.info_SpliterValue.getLocale(
                                      new Object[] {"[value]", "/" + aliasForCommands.getAlias()})
                                  + "\n");
                          commandsMessage
                              .addText(
                                  (aliasForCommands.isRequiresPerm()
                                          ? LC.modify_enabledColor.getLocale(new Object[0])
                                          : LC.modify_disabledColor.getLocale(new Object[0]))
                                      + CMILC.getIM(aliaseditor.class, "permRequirements"))
                              .addHover(
                                  LC.modify_ChangeHover.getLocale(new Object[0])
                                      + (aliasForCommands.isRequiresPerm()
                                          ? "\n"
                                              + CMILC.getIM(
                                                  aliaseditor.class,
                                                  "permReqExplanation",
                                                  "[alias]",
                                                  aliasForCommands.getAliasAsOneWordNS())
                                          : ""))
                              .addCommand(rawMessageCommand.getCommand());
                          rawMessageCommand.setKeep(true);
                          rawMessageCommand =
                              new RawMessageCommand() {
                                public void run(CommandSender clickSender) {
                                  aliasForCommands.setTabComplete(
                                      !aliasForCommands.isTabComplete());
                                  print();
                                  cmi.getAliasManager().saveNew();
                                }
                              };
                          commandsMessage
                              .addText(
                                  (aliasForCommands.isTabComplete()
                                          ? LC.modify_enabledColor.getLocale(new Object[0])
                                          : LC.modify_disabledColor.getLocale(new Object[0]))
                                      + CMILC.getIM(aliaseditor.class, "tab"))
                              .addHover(
                                  LC.modify_ChangeHover.getLocale(new Object[0])
                                      + (aliasForCommands.isTabComplete()
                                          ? "\n"
                                              + CMILC.getIM(
                                                  aliaseditor.class, "tabCompleteExplanation")
                                          : ""))
                              .addCommand(rawMessageCommand.getCommand());
                          rawMessageCommand.setKeep(true);
                          commandsMessage.addText(
                              (aliasForCommands.getCustomTabRawList() != null
                                          && !aliasForCommands.getCustomTabRawList().isEmpty()
                                      ? LC.modify_enabledColor.getLocale(new Object[0])
                                      : LC.modify_disabledColor.getLocale(new Object[0]))
                                  + CMILC.getIM(aliaseditor.class, "cTab"));
                          commandsMessage.addHover(aliasForCommands.getCustomTabRawList());
                          commandsMessage.addCommand(
                              CommandsHandler.getLabel()
                                  + " "
                                  + aliaseditor.class.getSimpleName()
                                  + " "
                                  + aliasForCommands.getAliasAsOneWord()
                                  + " customtab");
                          commandsMessage.addText(CMILC.getIM(aliaseditor.class, "file"));
                          commandsMessage.addHover(
                              CMILC.getIM(
                                  aliaseditor.class,
                                  "fileExplanation",
                                  "[file]",
                                  aliasForCommands.getFileName()));
                          commandsMessage.addCommand(
                              CommandsHandler.getLabel()
                                  + " "
                                  + aliaseditor.class.getSimpleName()
                                  + " "
                                  + aliasForCommands.getAliasAsOneWord()
                                  + " file");
                          rawMessageCommand =
                              new RawMessageCommand() {
                                public void run(CommandSender clickSender) {
                                  aliasForCommands.setAddTabs(!aliasForCommands.isAddTabs());
                                  print();
                                  cmi.getAliasManager().saveNew();
                                }
                              };
                          commandsMessage
                              .addText(
                                  (aliasForCommands.isAddTabs()
                                          ? LC.modify_enabledColor.getLocale(new Object[0])
                                          : LC.modify_disabledColor.getLocale(new Object[0]))
                                      + CMILC.getIM(
                                          aliaseditor.class,
                                          "addTabComplete",
                                          "[state]",
                                          aliasForCommands.isAddTabs()))
                              .addHover(CMILC.getIM(aliaseditor.class, "addTabExplanation"))
                              .addCommand(rawMessageCommand.getCommand());
                          rawMessageCommand.setKeep(true);
                          rawMessageCommand =
                              new RawMessageCommand() {
                                public void run(CommandSender clickSender) {
                                  aliasForCommands.setExact(!aliasForCommands.isExact());
                                  print();
                                  cmi.getAliasManager().saveNew();
                                }
                              };
                          commandsMessage
                              .addText(
                                  (aliasForCommands.isExact()
                                          ? LC.modify_enabledColor.getLocale(new Object[0])
                                          : LC.modify_disabledColor.getLocale(new Object[0]))
                                      + CMILC.getIM(
                                          aliaseditor.class,
                                          "exact",
                                          "[state]",
                                          aliasForCommands.isExact()))
                              .addHover(CMILC.getIM(aliaseditor.class, "addExactExplanation"))
                              .addCommand(rawMessageCommand.getCommand());
                          rawMessageCommand.setKeep(true);
                          commandsMessage.addText(
                              "\n"
                                  + LC.info_SpliterValue.getLocale(
                                      new Object[] {
                                        "[value]", LC.modify_ChangeCommands.getLocale(new Object[0])
                                      }));
                          commandsMessage.addCommand(
                              CommandsHandler.getLabel()
                                  + " "
                                  + aliaseditor.class.getSimpleName()
                                  + " "
                                  + aliasForCommands.getAliasAsOneWord()
                                  + " commands");
                          this.setTopLine(commandsMessage);
                        }
                      };
                  chatMessageListEdit.setFlatenLines(true);
                  chatMessageListEdit.setShortenLines(50);
                  chatMessageListEdit.print();
                  return true;
                }
                break;
              case 3143036:
                if (section.equals("file")) {
                  String currentFile = commandAlias.getFileName();
                  final CommandAlias aliasForFile = commandAlias;
                  ChatMessageEdit chatMessageEdit =
                      new ChatMessageEdit(player, currentFile) {
                        public void run(String newFileName) {
                          newFileName =
                              CMIChatColor.stripColor(CMIText.getFirstVariable(newFileName));
                          if (CMIText.isValidString(newFileName)) {
                            String oldFileName = aliasForFile.getFileName();
                            aliasForFile.setFileName(newFileName);
                            CMI.getInstance()
                                .performCommand(
                                    commandSender,
                                    "cmi aliaseditor "
                                        + aliasForFile.getAliasAsOneWord()
                                        + " commands");
                            cmi.getAliasManager().saveNew(oldFileName);
                            cmi.getAliasManager().saveNew(aliasForFile.getFileName());
                          }
                        }

                        public void onDisable() {
                          CMI.getInstance()
                              .performCommand(
                                  commandSender,
                                  "cmi aliaseditor "
                                      + aliasForFile.getAliasAsOneWord()
                                      + " commands");
                        }
                      };
                  chatMessageEdit.setCheckForCancel(true);
                  chatMessageEdit.printMessage();
                  return true;
                }
                break;
              case 1611575940:
                if (section.equals("customtab")) {
                  var list = commandAlias.getCustomTabRawList();
                  if (list == null) {
                    list = new ArrayList();
                    commandAlias.setCustomTab(list);
                  }

                  final CommandAlias aliasForTabs = commandAlias;
                  ChatMessageListEdit customTabEdit =
                      new ChatMessageListEdit(player, list, ChatEditType.String) {
                        public void onUpdate() {
                          cmi.getAliasManager().saveNew();
                          aliasForTabs.recheckTabCompletes();
                        }

                        public void beforePrint() {
                          RawMessage customTabMessage = new RawMessage();
                          customTabMessage.addText(
                              "\n"
                                  + LC.info_SpliterValue.getLocale(
                                      new Object[] {"[value]", "/" + aliasForCommands.getAlias()}));
                          customTabMessage.addText(
                              "\n" + CMILC.getIM(aliaseditor.class, "tabCompletes"));
                          this.setTopLine(customTabMessage);
                        }
                      };
                  customTabEdit.print();
                  return true;
                }
            }
          }

          if (strings.length == 2) {
            String subCommand;
            switch ((subCommand = strings[1]).hashCode()) {
              case -934610812:
                if (subCommand.equals("remove")) {
                  cmi.getAliasManager().removeCustom(commandAlias);
                  cmi.performCommand(commandSender, "cmi aliaseditor");
                  cmi.getAliasManager().saveNew();
                  return true;
                }
                break;
              case 96417:
                if (subCommand.equals("add")) {
                  cmi.getCTextManager()
                      .addChatEditor(
                          player, "cmi aliaseditor " + commandAlias.getAliasAsOneWord() + " add ");
                  RawMessage addMessage = new RawMessage();
                  addMessage
                      .addText(CMILC.getIM(this, "addInfo"))
                      .addHover(CMILC.getIM(this, "addInformationHover"));
                  addMessage.show(commandSender);
                  return true;
                }
            }
          }
        }

        return false;
      }
    }
  }
}
