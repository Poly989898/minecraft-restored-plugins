package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.DataBase.DBDAO;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.UUID;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class migratedatabase implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Changes database system and migrates all data",
      regVar = 666,
      consoleVar = 0,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (!Bukkit.getOnlinePlayers().isEmpty()) {
      cmi.getConfigManager().setMaintenance(true);
      cmi.getConfigManager().setMaintenanceMessage(CMILC.getIM("maintenance", "feedback"));
      String string = CMILC.getIM("maintenance", "feedback");

      for (Player player : Bukkit.getOnlinePlayers()) {
        try {
          player.kickPlayer(string);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }

    cmi.getDbManager().saveBatch(true);
    CMIScheduler.runTaskLater(
        cmi,
        () -> {
          try {
            cmi.getDbManager().switchDataBase();
          } catch (java.sql.SQLException sqlException) {
            sqlException.printStackTrace();
          }
          if (cmi.getDbManager().getDB() != null && cmi.getDbManager().getDB().isConnected()) {
            cmi.getDbManager().getDB().truncate(DBDAO.DBTables.UserTable.getTableName());
            cmi.getDbManager().getDB().truncate(DBDAO.DBTables.InvTable.getTableName());
            cmi.getConfigManager()
                .ChangeConfig("storage.method", cmi.getDbManager().getDbType().name());

            for (var entry : cmi.getPlayerManager().getAllUsers().entrySet()) {
              ((CMIUser) entry.getValue()).setId(0);
              ((CMIUser) entry.getValue()).setInvId(0);
              ((CMIUser) entry.getValue()).addForDelayedSave();
            }

            int value = 0;

            for (var userEntry : cmi.getPlayerManager().getAllUsers().entrySet()) {
              ((CMIUser) userEntry.getValue()).setInvId(0);
              if (cmi.getSavedInventoryManager().saveAllInventories((UUID) userEntry.getKey())) {
                value++;
              }
            }

            CMILC.sendMessage(
                commandSender, "&eSaved &6" + value + " &eusers inventories to new data base!");
            cmi.getConfigManager().reload(commandSender);
            CMILC.sendMessage(
                commandSender,
                "&eSwitched to new database system &6" + cmi.getDbManager().getDbType());
            CMILC.sendMessage(
                commandSender,
                "&eSaving &6"
                    + cmi.getPlayerManager().getAllUsers().size()
                    + " &eusers data to new data base! This might take some time depending on your"
                    + " database size. Wait until its finishes before rejoining server");
            cmi.getDbManager().startRapidSave();
            cmi.getDbManager().saveBatch(false);
          } else {
            CMILC.sendMessage(
                commandSender,
                "&cCant establish connection with (&6"
                    + cmi.getDbManager().getDbType().name()
                    + "&c) data base! Check your config file!");
            try {
              cmi.getDbManager().switchDataBase();
            } catch (java.sql.SQLException sqlException) {
              sqlException.printStackTrace();
            }
          }
        },
        10L);
    return true;
  }
}
