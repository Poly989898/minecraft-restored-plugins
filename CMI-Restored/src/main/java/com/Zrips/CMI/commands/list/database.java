package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Backup.CMIBackUpManager;
import com.Zrips.CMI.Modules.Backup.CMIBackupFeedback;
import com.Zrips.CMI.Modules.DataBase.DBDAO;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashSet;
import java.util.Set;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class database implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "backupSuccess", "{gcp}Backup created with name {gcs}[name] {gcp}Tables: {gcs}[types]");
    configReader.get("backupFailed", "{gcp}Backup failed");
    configReader.get(
        "userDataRestored", "{gcp}Restored {gcs}[playerName] {gcp}data. Tables: {gcs}[types]");
    configReader.get("userDataFailed", "{gcw}Restoring failed for {gcs}[playerName]");
  }

  @CAnnotation(
      info = "Manage backup data",
      args = "[action] (playerName) (dataType) (fileName)",
      multiTab = {
        "restoreplayer [playerName] ||usertable,inventories,playtime,playtimereward",
        "backup ||usertable,inventories,playtime,playtimereward"
      },
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {0, 1, 2, 3, 4})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    database.Action action = null;
    HashSet<com.Zrips.CMI.Modules.DataBase.DBDAO.DBTables> hashSet = new HashSet();
    String string = null;
    String text = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String textSecond = parts[count];
      if (action == null) {
        action = database.Action.get(textSecond);
        if (action != null) {
          continue;
        }
      }

      if (string == null) {
        CMIUser cmiUser = CMIUser.getUser(textSecond);
        if (cmiUser != null) {
          string = textSecond;
          continue;
        }
      }

      DBDAO.DBTables dbTables = DBDAO.DBTables.get(textSecond);
      if (dbTables != null) {
        hashSet.add(dbTables);
      } else if (textSecond.endsWith(".db")
          || textSecond.contains("cmi.sqlite")
          || textSecond.contains(".")) {
        text = textSecond;
      }
    }

    if (action == null) {
      action = database.Action.list;
    }

    switch (action) {
      case restoreplayer:
        CMIUser cMIUserSecond = cmi.getUser(commandSender, string, this);
        if (cMIUserSecond == null) {
          return null;
        }

        if (hashSet.isEmpty()) {
          DBDAO.DBTables[] dbTableses;
          int countSecond = (dbTableses = DBDAO.DBTables.values()).length;

          for (int countThird = 0; countThird < countSecond; countThird++) {
            DBDAO.DBTables dbTablesValue = dbTableses[countThird];
            hashSet.add(dbTablesValue);
          }
        }

        if (text != null) {
          CMIBackUpManager.getFileAsync(text)
              .thenAccept(
                  file -> {
                    if (file == null) {
                      CMILC.info_IncorrectFile.sendMessage(commandSender);
                    } else {
                      CMIBackUpManager.restoreUserFromBackup(
                              commandSender, cMIUserSecond, file, convert(hashSet))
                          .whenComplete(
                              (cmiBackupFeedback, throwable) ->
                                  playerFeedback(cmiBackupFeedback, commandSender, cMIUserSecond));
                    }
                  });
        } else {
          CMIBackUpManager.restoreUserFromBackup(commandSender, cMIUserSecond, convert(hashSet))
              .whenComplete(
                  (cmiBackupFeedbackValue, backupError) ->
                      playerFeedback(cmiBackupFeedbackValue, commandSender, cMIUserSecond));
        }
        break;
      case backup:
        if (commandSender instanceof Player) {
          LC.info_FromConsole.sendMessage(commandSender, new Object[0]);
          return null;
        }

        if (hashSet.isEmpty()) {
          hashSet.add(DBDAO.DBTables.UserTable);
          hashSet.add(DBDAO.DBTables.PlayTimeReward);
        }

        if (text != null) {
          CMIBackUpManager.backupDataBase(text, convert(hashSet))
              .whenComplete(
                  (cmiBackupFeedbackSecond, secondBackupError) ->
                      backupFeedback(cmiBackupFeedbackSecond, commandSender));
        } else {
          CMIBackUpManager.backupDataBase(convert(hashSet))
              .whenComplete(
                  (cmiBackupFeedbackThird, thirdBackupError) ->
                      backupFeedback(cmiBackupFeedbackThird, commandSender));
        }
        break;
      case list:
        return false;
    }

    return true;
  }

  private static void backupFeedback(
      CMIBackupFeedback cmiBackupFeedback, CommandSender commandSender) {
    if (cmiBackupFeedback.getFile() != null) {
      CMILC.info(
          database.class,
          commandSender,
          "backupSuccess",
          "[name]",
          cmiBackupFeedback.getFile().getName(),
          "[types]",
          getTablesFeedbackMessage(cmiBackupFeedback));
    } else {
      CMILC.info(database.class, commandSender, "backupFailed");
    }
  }

  private static String getTablesFeedbackMessage(CMIBackupFeedback cmiBackupFeedback) {
    StringBuilder stringBuilder = new StringBuilder();

    for (var entry : cmiBackupFeedback.getTables().entrySet()) {
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
      }

      stringBuilder.append(
          (entry.getValue()
                  ? LC.modify_enabledColor.getLocale(new Object[0])
                  : LC.modify_disabledColor.getLocale(new Object[0]))
              + ((DBDAO.DBTables) entry.getKey()).getTableNameOnly());
    }

    return stringBuilder.toString();
  }

  private static void playerFeedback(
      CMIBackupFeedback cmiBackupFeedback, CommandSender commandSender, CMIUser cmiUser) {
    boolean flag = false;

    for (Boolean flagSecond : cmiBackupFeedback.getTables().values()) {
      if (flagSecond) {
        flag = true;
      }
    }

    if (flag) {
      CMILC.info(
          database.class,
          commandSender,
          "userDataRestored",
          cmiUser,
          "[types]",
          getTablesFeedbackMessage(cmiBackupFeedback));
    } else {
      CMILC.info(database.class, commandSender, "userDataFailed", cmiUser);
    }
  }

  private static DBDAO.DBTables[] convert(Set<DBDAO.DBTables> set) {
    HashSet<DBDAO.DBTables> hashSet = new HashSet<>();
    return set != null && !set.isEmpty()
        ? set.toArray(new DBDAO.DBTables[0])
        : hashSet.toArray(new DBDAO.DBTables[0]);
  }

  private enum Action {
    restoreplayer,
    backup,
    list;

    public static database.Action get(String string) {
      database.Action[] actions;
      int value = (actions = values()).length;

      for (int count = 0; count < value; count++) {
        database.Action action = actions[count];
        if (action.toString().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
