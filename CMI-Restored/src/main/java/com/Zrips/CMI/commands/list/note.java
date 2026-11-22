package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.PlayerNote;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.DateFormat;
import java.util.List;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class note implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("newNote", "{gcp}Added new note for player");
    configReader.get("clearNote", "{gcp}All notes cleared for player");
    configReader.get("removedNote", "{gcp}Removed note for player");
    configReader.get("noNotesById", "{gcp}There is no saved notes by this id for player");
    configReader.get("noNotes", "{gcp}There is no saved notes for player");
    configReader.get("listNotes", "{gcs}[note]");
  }

  @CAnnotation(
      priority = 180,
      info = "Manage players notes",
      args = "(playerName) [add/remove/clear/list] (id/note)",
      tab = {"playername", "add%%remove%%clear%%list"},
      regVar = -100,
      consoleVar = -100,
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, final CommandSender commandSender, String[] strings) {
    note.Action action = null;
    int value = 0;
    String string = null;
    String text = "";
    String[] parts = strings;
    int count = strings.length;

    for (int countSecond = 0; countSecond < count; countSecond++) {
      String textSecond = parts[countSecond];
      if (action == null) {
        String textThird;
        switch ((textThird = textSecond.toLowerCase()).hashCode()) {
          case -934610812:
            if (textThird.equals("remove")) {
              action = note.Action.Remove;
              continue;
            }
            break;
          case 96417:
            if (textThird.equals("add")) {
              action = note.Action.Add;
              continue;
            }
            break;
          case 3322014:
            if (textThird.equals("list")) {
              action = note.Action.List;
              continue;
            }
            break;
          case 94746189:
            if (textThird.equals("clear")) {
              action = note.Action.Clear;
              continue;
            }
        }
      }

      if (action != null && action == note.Action.Remove) {
        try {
          value = Integer.parseInt(textSecond);
          continue;
        } catch (NumberFormatException numberFormatException) {
        }
      }

      if (string == null && action == null) {
        string = textSecond;
      } else {
        if (!text.isEmpty()) {
          text = text + " ";
        }

        text = text + textSecond;
      }
    }

    final CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (action == null) {
      action = note.Action.List;
    }

    boolean flag = false;
    switch (action) {
      case List:
        flag = true;
        break;
      case Add:
        if (!PermissionsManager.CMIPerm.command_note_add.hasPermission(commandSender, true)) {
          return null;
        }

        if (text.length() <= 2) {
          return false;
        }

        cmiUser.addNote(
            new PlayerNote(commandSender.getName(), System.currentTimeMillis(), text), true);
        CMILC.info(this, commandSender, "newNote");
        flag = true;
        break;
      case Clear:
        if (!PermissionsManager.CMIPerm.command_note_remove.hasPermission(commandSender, true)) {
          return null;
        }

        cmiUser.getNotes().clear();
        cmiUser.addForDelayedSave();
        break;
      case Remove:
        if (!PermissionsManager.CMIPerm.command_note_remove.hasPermission(commandSender, true)) {
          return null;
        }

        PlayerNote playerNote = null;

        try {
          playerNote = cmiUser.getNotes().remove(value - 1);
        } catch (Exception exception) {
        }

        if (playerNote != null) {
          cmiUser.addForDelayedSave();
          CMILC.info(this, commandSender, "removedNote");
        } else {
          CMILC.info(this, commandSender, "noNotesById");
        }

        flag = true;
    }

    if (flag) {
      var list = cmiUser.getNotes();
      if (list.isEmpty()) {
        CMILC.info(this, commandSender, "noNotes");
        return true;
      }

      PageInfo pageInfo = new PageInfo(50, list.size(), 1);
      ChatMessageObjectEdit chatMessageObjectEdit =
          new ChatMessageObjectEdit(commandSender, pageInfo);
      chatMessageObjectEdit.setAddNew(false);

      for (int countThird = 0; countThird < list.size(); countThird++) {
        PlayerNote playerNoteValue = (PlayerNote) list.get(countThird);
        final int countFourth = countThird;
        ChatEditorObject chatEditorObject =
            new ChatEditorObject(
                CMILC.getIM(
                    this,
                    "listNotes",
                    "[note]",
                    CMIChatColor.translate(playerNoteValue.getNote()))) {
              public void onDelete() {
                CMI.getInstance()
                    .performCommand(
                        commandSender,
                        CommandsHandler.getLabel()
                            + " "
                            + note.class.getSimpleName()
                            + " "
                            + cmiUser.getName()
                            + " remove "
                            + (countFourth + 1));
              }
            };
        chatEditorObject.setHover(
            playerNoteValue.getSender() + " \n" + DateFormat.MiliToDate(playerNoteValue.getTime()));
        chatMessageObjectEdit.addline(chatEditorObject);
      }

      chatMessageObjectEdit.print();
    }

    return true;
  }

  private enum Action {
    List,
    Add,
    Clear,
    Remove;
  }
}
