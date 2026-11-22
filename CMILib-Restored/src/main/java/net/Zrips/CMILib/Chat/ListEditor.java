package net.Zrips.CMILib.Chat;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMICommands;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.commands.CommandsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListEditor {
  private static HashMap<UUID, String> chatMap = new HashMap<>();
  private static HashMap<UUID, ShadowCommand> shadowCommand = new HashMap<>();
  public static final String questionMarkReplacer = "<>-<>";

  public static String addShadowCmd(
      Player player, String cmd, Boolean infinite, ShadowCommandType type) {
    String id = new Random().nextInt(Integer.MAX_VALUE) + (infinite ? "i" : "");
    ShadowCommand shadow = shadowCommand.getOrDefault(player.getUniqueId(), new ShadowCommand());
    id = shadow.add(id, cmd, type);
    shadowCommand.put(player.getUniqueId(), shadow);
    return id;
  }

  public static ShadowCommandInfo getShadowCommand(Player player, String id) {
    ShadowCommand shadow = shadowCommand.get(player.getUniqueId());
    return shadow == null ? null : shadow.get(id);
  }

  public static boolean isChatEditing(Player player) {
    return chatMap.containsKey(player.getUniqueId());
  }

  public static void addChatEditor(Player player, String cmd) {
    if (player != null) {
      if (!cmd.endsWith(" ")) {
        cmd = cmd + " ";
      }

      chatMap.put(player.getUniqueId(), cmd);
    }
  }

  public static void removeChatEditor(Player player) {
    chatMap.remove(player.getUniqueId());
  }

  public static String getChatEditorCmd(Player player) {
    return chatMap.get(player.getUniqueId());
  }

  public static List<String> move(
      List<String> list, int place, ListEditor.listMoveDirection direction) {
    int from = place;
    int to = from + direction.getDir();
    String tocmd = "";
    String fromcmd = "";
    if (list.size() >= to + 1 && to >= 0) {
      tocmd = list.get(to);
    }

    if (list.size() >= from + 1 && from >= 0) {
      fromcmd = list.get(from);
    }

    if (!tocmd.isEmpty() && !fromcmd.isEmpty()) {
      list.set(to, fromcmd);
      list.set(from, tocmd);
    }

    return list;
  }

  public static boolean processListEditing(
      String[] args,
      CommandSender sender,
      String cmd,
      String name,
      List<String> list,
      boolean add,
      String subCommand,
      boolean fillEmpty,
      ListEditor.listEditorType type) {
    String cmdprefix = CommandsHandler.getLabel() + " " + cmd + " " + subCommand + " " + name;
    if (args.length == 2) {
      int i = 0;
      RawMessage rm = new RawMessage();
      rm.show(sender);

      for (String one : list) {
        rm = new RawMessage();
        rm.addText(LC.modify_listUpSymbol.getLocale() + " ")
            .addHover(LC.modify_listUpSymbolHover.getLocale())
            .addCommand(cmdprefix + " moveup " + i);
        rm.addText(LC.modify_listDownSymbol.getLocale() + " ")
            .addHover(LC.modify_listDownSymbolHover.getLocale())
            .addCommand(cmdprefix + " movedown " + i);
        if ((one.isEmpty() || one.equalsIgnoreCase(" ")) && fillEmpty) {
          rm.addText(LC.modify_commandList.getLocale("[command]", LC.modify_emptyLine.getLocale()))
              .addHover(LC.modify_editSymbolHover.getLocale("[text]", ""))
              .addCommand(cmdprefix + " edit " + i);
        } else {
          rm.addText(LC.modify_commandList.getLocale("[command]", one))
              .addHover(LC.modify_editSymbolHover.getLocale("[text]", ""))
              .addCommand(cmdprefix + " edit " + i);
        }

        rm.addText(LC.modify_deleteSymbol.getLocale())
            .addHover(LC.modify_deleteSymbolHover.getLocale("[text]", one))
            .addCommand(cmdprefix + " remove " + i);
        rm.show(sender);
        i++;
      }

      rm = new RawMessage();
      if (add) {
        rm.addText(LC.modify_addSymbol.getLocale())
            .addHover(LC.modify_addSymbolHover.getLocale())
            .addCommand(cmdprefix + " add");
      }

      rm.show(sender);
      return true;
    } else {
      Player player = null;
      if (sender instanceof Player) {
        player = (Player) sender;
      }

      if (args.length < 2) {
        return false;
      }

      if (name == null || cmd == null) {
        return false;
      }

      if (args.length == 3) {
        switch (args[2]) {
          case "add":
            addChatEditor(player, cmdprefix + " add");
            RawMessage rm = new RawMessage();
            switch (type) {
              case text:
                rm.addText(LC.modify_lineAddInfo);
                break;
              case commands:
                rm.addText(LC.modify_commandAddInfo).addHover(LC.modify_commandAddInformationHover);
            }

            rm.show(player);
            return true;
        }
      }

      switch (args[2]) {
        case "remove":
          int line = -1;

          try {
            line = Integer.parseInt(args[3]);
          } catch (Exception e) {
            return false;
          }

          if (list.size() > line) {
            list.remove(line);
          }

          CMICommands.performCommand(player, cmdprefix);
          return true;
        case "moveup":
          int upLine = -1;

          try {
            upLine = Integer.parseInt(args[3]);
          } catch (Exception e) {
            return false;
          }

          move(list, upLine, ListEditor.listMoveDirection.Up);
          CMICommands.performCommand(player, cmdprefix);
          return true;
        case "movedown":
          int downLine = -1;

          try {
            downLine = Integer.parseInt(args[3]);
          } catch (Exception e) {
            return false;
          }

          move(list, downLine, ListEditor.listMoveDirection.Down);
          CMICommands.performCommand(player, cmdprefix);
          return true;
        case "add":
          if (add) {
            if (args.length == 4 && args[3].equalsIgnoreCase("cancel")
                || args.length == 5 && args[4].equalsIgnoreCase("cancel")) {
              removeChatEditor(player);
              CMICommands.performCommand(player, cmdprefix);
              return true;
            }

            String cmds = "";

            for (int i = 3; i < args.length; i++) {
              if (!cmds.isEmpty()) {
                cmds = cmds + " ";
              }

              cmds = cmds + args[i];
            }

            list.add(cmds);
            CMICommands.performCommand(player, cmdprefix);
          }

          return true;
        case "edit":
          int editLine = -1;

          try {
            editLine = Integer.parseInt(args[3]);
          } catch (Exception e) {
            return false;
          }

          if (args.length == 4) {
            addChatEditor(player, cmdprefix + " edit " + editLine + " ");
            RawMessage rm = new RawMessage();
            rm.addText(LC.modify_commandEditInfo)
                .addHover(LC.modify_commandEditInfoHover)
                .addSuggestion(list.get(editLine));
            rm.show(player);
            return true;
          } else {
            if ((args.length != 5 || !args[4].equalsIgnoreCase("cancel"))
                && (args.length != 4 || !args[3].equalsIgnoreCase("cancel"))) {
              if ((args.length != 5 || !args[4].equalsIgnoreCase("remove"))
                  && (args.length != 4 || !args[3].equalsIgnoreCase("remove"))) {
                String cmdText = "";

                for (int i = 4; i < args.length; i++) {
                  if (!cmdText.isEmpty()) {
                    cmdText = cmdText + " ";
                  }

                  cmdText = cmdText + args[i];
                }

                list.set(editLine, cmdText);
                CMICommands.performCommand(player, cmdprefix);
                return true;
              }

              removeChatEditor(player);
              list.remove(editLine);
              CMICommands.performCommand(player, cmdprefix);
              return true;
            }

            removeChatEditor(player);
            CMICommands.performCommand(player, cmdprefix);
            return true;
          }
        default:
          break;
      }
    }
    return false;
  }

  public enum listEditorType {
    text,
    commands;
  }

  public enum listMoveDirection {
    Up(-1),
    Down(1);

    private int dir;

    listMoveDirection(int dir) {
      this.dir = dir;
    }

    public int getDir() {
      return this.dir;
    }

    public void setDir(int dir) {
      this.dir = dir;
    }
  }
}
