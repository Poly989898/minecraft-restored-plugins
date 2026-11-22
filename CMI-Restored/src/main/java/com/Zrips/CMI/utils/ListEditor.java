package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.Chat.listMoveDirection;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.entity.Player;

public class ListEditor {
  public static List<String> move(
      List<String> list, int value, listMoveDirection listMoveDirection) {
    int newPosition = value;
    int clampedPosition = newPosition + listMoveDirection.getDir();
    String string = "";
    String direction = "";
    if (list.size() >= clampedPosition + 1 && clampedPosition >= 0) {
      string = (String) list.get(clampedPosition);
    }

    if (list.size() >= newPosition + 1 && newPosition >= 0) {
      direction = (String) list.get(newPosition);
    }

    if (!string.isEmpty() && !direction.isEmpty()) {
      list.set(clampedPosition, direction);
      list.set(newPosition, string);
    }

    return list;
  }

  public static boolean showListForEditing(
      Player player, String string, String label, List<String> list, boolean flag) {
    return showListForEditing(player, string, label, list, flag, false);
  }

  public static boolean showListForEditing(
      Player player, String string, String label, List<String> list, boolean flag, boolean silent) {
    int value = 0;
    RawMessage rawMessage = new RawMessage();
    rawMessage.add(
        LC.modify_commandTitle.getLocale(new Object[] {"[name]", label}),
        LC.modify_commandEdit.getLocale(new Object[0]),
        "cmi " + string + " edit ");
    rawMessage.show(player);

    for (String item : list) {
      rawMessage = new RawMessage();
      rawMessage.add(
          LC.modify_listUpSymbol.getLocale(new Object[0]) + " ",
          LC.modify_listUpSymbolHover.getLocale(new Object[0]),
          "cmi " + string + " moveup " + value);
      rawMessage.add(
          LC.modify_listDownSymbol.getLocale(new Object[0]) + " ",
          LC.modify_listDownSymbolHover.getLocale(new Object[0]),
          "cmi " + string + " movedown " + value);
      if ((item.isEmpty() || item.equalsIgnoreCase(" ")) && silent) {
        rawMessage.add(
            LC.modify_commandList.getLocale(
                new Object[] {"[command]", LC.modify_emptyLine.getLocale(new Object[0])}),
            LC.modify_editSymbolHover.getLocale(new Object[] {"[text]", ""}),
            "cmi " + string + " edit " + value);
      } else {
        rawMessage.add(
            LC.modify_commandList.getLocale(new Object[] {"[command]", item}),
            LC.modify_editSymbolHover.getLocale(new Object[] {"[text]", ""}),
            "cmi " + string + " edit " + value);
      }

      rawMessage.add(
          LC.modify_deleteSymbol.getLocale(new Object[0]),
          LC.modify_deleteSymbolHover.getLocale(new Object[] {"[text]", item}),
          "cmi " + string + " remove " + value);
      rawMessage.show(player);
      value++;
    }

    rawMessage = new RawMessage();
    if (flag) {
      rawMessage.add(
          LC.modify_addSymbol.getLocale(new Object[0]),
          LC.modify_addSymbolHover.getLocale(new Object[0]),
          "cmi " + string + " add");
    }

    rawMessage.show(player);
    return true;
  }

  public static boolean processEditListClean(
      String[] strings,
      Player player,
      String string,
      String prefix,
      List<String> list,
      boolean flag,
      String suffix) {
    if (suffix != null) {
      strings = Arrays.copyOfRange(strings, 1, strings.length);
    }

    String commandLabel = CommandsHandler.getLabel();
    if (strings.length >= 2) {
      if (prefix == null || string == null) {
        return false;
      }

      if (strings.length == 2) {
        String itemText;
        switch ((itemText = strings[1]).hashCode()) {
          case 96417:
            if (itemText.equals("add")) {
              CMI.getInstance()
                  .getCTextManager()
                  .addChatEditor(player, commandLabel + " " + string + " add ");
              RawMessage rawMessage = new RawMessage();
              rawMessage.add(
                  LC.modify_lineAddInfo.getLocale(new Object[0]),
                  LC.modify_commandEditInfoHover.getLocale(new Object[0]));
              rawMessage.show(player);
              return true;
            }
        }
      }

      String lineText;
      switch ((lineText = strings[1]).hashCode()) {
        case -1068262868:
          if (lineText.equals("moveup")) {
            int value = -1;

            try {
              value = Integer.parseInt(strings[2]);
            } catch (Exception exception) {
              return false;
            }

            move(list, value, listMoveDirection.Up);
            CMI.getInstance().performCommand(player, commandLabel + " " + string + " ");
            return true;
          }
          break;
        case -934610812:
          if (lineText.equals("remove")) {
            int startIndex = -1;

            try {
              startIndex = Integer.parseInt(strings[2]);
            } catch (Exception exception2) {
              return false;
            }

            list.remove(startIndex);
            CMI.getInstance().performCommand(player, commandLabel + " " + string + " ");
            return true;
          }
          break;
        case -103936013:
          if (lineText.equals("movedown")) {
            int endIndex = -1;

            try {
              endIndex = Integer.parseInt(strings[2]);
            } catch (Exception exception3) {
              return false;
            }

            move(list, endIndex, listMoveDirection.Down);
            CMI.getInstance().performCommand(player, commandLabel + " " + string + " ");
            return true;
          }
          break;
        case 96417:
          if (lineText.equals("add")) {
            if (flag) {
              if (strings.length > 2 && strings[2].equalsIgnoreCase("cancel")) {
                CMI.getInstance().getCTextManager().removeChatEditor(player);
                CMI.getInstance().performCommand(player, commandLabel + " " + string + " ");
                return true;
              }

              String commandText = "";

              for (int argIndex = 2; argIndex < strings.length; argIndex++) {
                if (!commandText.isEmpty()) {
                  commandText = commandText + " ";
                }

                commandText = commandText + strings[argIndex];
              }

              list.add(commandText);
              CMI.getInstance().performCommand(player, commandLabel + " " + string + " ");
            }

            return true;
          }
          break;
        case 3108362:
          if (lineText.equals("edit")) {
            int count = -1;

            try {
              count = Integer.parseInt(strings[2]);
            } catch (Exception exception4) {
              return false;
            }

            if (strings.length == 3) {
              CMI.getInstance()
                  .getCTextManager()
                  .addChatEditor(player, commandLabel + " " + string + " edit " + count + " ");
              RawMessage message = new RawMessage();
              message.add(
                  LC.modify_commandEditInfo.getLocale(new Object[0]),
                  LC.modify_commandEditInfoHover.getLocale(new Object[0]),
                  null,
                  (String) list.get(count));
              message.show(player);
              return true;
            }

            if (strings.length == 4 && strings[3].equalsIgnoreCase("cancel")) {
              CMI.getInstance().getCTextManager().removeChatEditor(player);
              CMI.getInstance().performCommand(player, commandLabel + " " + string + " ");
              return true;
            }

            if (strings.length == 4 && strings[3].equalsIgnoreCase("remove")) {
              CMI.getInstance().getCTextManager().removeChatEditor(player);
              list.remove(count);
              CMI.getInstance().performCommand(player, commandLabel + " " + string + " ");
              return true;
            }

            String tailText = "";

            for (int tailIndex = 3; tailIndex < strings.length; tailIndex++) {
              if (!tailText.isEmpty()) {
                tailText = tailText + " ";
              }

              tailText = tailText + strings[tailIndex];
            }

            list.set(count, tailText);
            CMI.getInstance().performCommand(player, commandLabel + " " + string + " ");
            return true;
          }
      }
    }

    return false;
  }

  public enum listEditorType {
    text,
    commands;
  }
}
