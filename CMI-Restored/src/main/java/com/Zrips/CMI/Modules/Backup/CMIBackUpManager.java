package com.Zrips.CMI.Modules.Backup;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.DataBase.DBDAO;
import com.Zrips.CMI.Modules.DataBase.DBSQLite;
import com.Zrips.CMI.utils.CMIThread;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nonnull;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.command.CommandSender;

public class CMIBackUpManager {
  private static String databaseBackups = "DatabaseBackups";
  private static CMITask task;
  private static boolean enabled = true;
  private static boolean zip = true;
  private static int maxBackups = 10;
  private static int intervalBetween = 24;
  private static Set<DBDAO.DBTables> autoTablesToSave = new HashSet<>();
  private static Set<DBDAO.DBTables> manualTablesToSave = new HashSet<>();
  private static String defaultSuffix = "cmi.sqlite.db";
  private static boolean unzipping = false;

  private static void verifyFolder() {
    try {
      File file = new File(CMI.getInstance().getDataFolder(), databaseBackups);
      if (!file.isDirectory()) {
        file.mkdirs();
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void onStop() {
    if (task != null) {
      task.cancel();
      task = null;
    }
  }

  public static void loadConfig() {
    CMI cmi = CMI.getInstance();
    ConfigReader configReader = cmi.getConfigManager().getConfig();

    try {
      configReader = new ConfigReader(cmi, "Settings" + File.separator + "BackUps.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    }

    configReader.addComment(
        "AutoBackup.Enabled",
        new String[] {
          "When enabled automatic database backup will be done if existing file is older than"
              + " specified interval"
        });
    enabled = configReader.get("AutoBackup.Enabled", true);
    configReader.addComment(
        "AutoBackup.Max",
        new String[] {
          "Amount of auto backup files we should keep",
          "Auto backup files will have specific format like 2025-09-04_13-20-42_" + defaultSuffix
        });
    maxBackups = configReader.get("AutoBackup.Max", 10);
    configReader.addComment(
        "AutoBackup.Zip", new String[] {"Do you wan't to zip database files to make them smaller"});
    zip = configReader.get("AutoBackup.Zip", true);
    configReader.addComment(
        "AutoBackup.TablesToSave",
        new String[] {
          "Tables you want to save with auto saves",
          "Recommendation is to skip bigger tables and only save most important ones",
          "Options: usertable inventories playtime playtimereward",
          "UserTable is always saved to be used for player ID refference when extracting data from"
              + " backup"
        });

    for (String string :
        configReader.get("AutoBackup.TablesToSave", Arrays.asList("usertable", "playtimereward"))) {
      autoTablesToSave.add(DBDAO.DBTables.get(string.toUpperCase()));
    }

    configReader.addComment(
        "AutoBackup.Interval",
        new String[] {
          "Inteval in hours between backups",
          "Keep in mind that backuping happens shortly after server startup and gets checked once"
              + " every hour on regular basis"
        });
    intervalBetween = configReader.get("AutoBackup.Interval", 24);
    configReader.addComment(
        "ManualBackup.TablesToSave",
        new String[] {
          "Tables you want to save when manually initiating backup",
          "Options: usertable inventories playtime playtimereward",
          "UserTable is always saved to be used for player ID refference when extracting data from"
              + " backup"
        });

    for (String item :
        configReader.get(
            "ManualBackup.TablesToSave", Arrays.asList("usertable", "playtimereward"))) {
      manualTablesToSave.add(DBDAO.DBTables.get(item.toUpperCase()));
    }

    configReader.save();
    CMIScheduler.runTaskLater(CMI.getInstance(), () -> startSaveTasker(), 100L);
  }

  private static void startSaveTasker() {
    if (task != null) {
      task.cancel();
      task = null;
    }

    if (enabled) {
      task =
          CMIScheduler.runTimerAsync(
              CMI.getInstance(),
              () -> {
                File file = getLatestBackup();
                long longValue = Long.MAX_VALUE;
                if (file != null) {
                  longValue = System.currentTimeMillis() - file.lastModified();
                }

                if (longValue > intervalBetween * 60L * 60L * 1000L) {
                  backupDataBase(autoTablesToSave.toArray(new DBDAO.DBTables[0]));
                }
              },
              1200L,
              72000L);
    }
  }

  // $VF: Could not inline inconsistent finally blocks
  // Please report this to the Vineflower issue tracker, at
  // https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the
  // rights to distribute it!)
  public static File zipFile(File file) {
    if (file.exists() && file.isFile()) {
      File zipFile = new File(file.getAbsolutePath() + ".zip");

      try {
        Throwable throwable = null;
        Object object = null;

        try {
          FileOutputStream fileOutputStream = new FileOutputStream(zipFile);

          try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            try {
              FileInputStream fileInputStream = new FileInputStream(file);

              try {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);
                byte[] bs = new byte[4096];

                int value;
                while ((value = fileInputStream.read(bs)) > 0) {
                  zipOutputStream.write(bs, 0, value);
                }

                zipOutputStream.closeEntry();
                CMIMessages.consoleMessage("[CMI] Backup compacted: " + zipFile.getName());
              } finally {
                if (fileInputStream != null) {
                  fileInputStream.close();
                }
              }
            } catch (Throwable throwable2) {
              if (throwable == null) {
                throwable = throwable2;
              } else if (throwable != throwable2) {
                throwable.addSuppressed(throwable2);
              }

              if (zipOutputStream != null) {
                zipOutputStream.close();
              }

              throw CMIBackUpManager.<IOException>sneakyThrow(throwable);
            }

            if (zipOutputStream != null) {
              zipOutputStream.close();
            }
          } catch (Throwable throwable3) {
            if (throwable == null) {
              throwable = throwable3;
            } else if (throwable != throwable3) {
              throwable.addSuppressed(throwable3);
            }

            if (fileOutputStream != null) {
              fileOutputStream.close();
            }

            throw CMIBackUpManager.<IOException>sneakyThrow(throwable);
          }

          if (fileOutputStream != null) {
            fileOutputStream.close();
          }

          return zipFile;
        } catch (Throwable throwable4) {
          if (throwable == null) {
            throwable = throwable4;
          } else if (throwable != throwable4) {
            throwable.addSuppressed(throwable4);
          }

          throw CMIBackUpManager.<IOException>sneakyThrow(throwable);
        }
      } catch (IOException ioException) {
        ioException.printStackTrace();
        return null;
      }
    } else {
      CMIMessages.consoleMessage("Database file does not exist: " + file.getAbsolutePath());
      return null;
    }
  }

  // $VF: Could not inline inconsistent finally blocks
  // Please report this to the Vineflower issue tracker, at
  // https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the
  // rights to distribute it!)
  public static File unzipFile(CommandSender commandSender, File file) {
    if (unzipping) {
      CMIMessages.sendMessage(commandSender, "File is already being unzipped, try again later");
      return null;
    }

    if (file.exists() && file.isFile()) {
      unzipping = true;
      File parentDir = file.getParentFile();
      File targetFile = null;

      try {
        Throwable throwable = null;
        Object object = null;

        try {
          FileInputStream fileInputStream = new FileInputStream(file);

          try {
            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);

            try {
              ZipEntry zipEntry;
              if ((zipEntry = zipInputStream.getNextEntry()) != null) {
                targetFile = new File(parentDir, zipEntry.getName());
                Throwable primaryError = null;
                Object resourceRef = null;

                try {
                  FileOutputStream fileOutputStream = new FileOutputStream(targetFile);

                  try {
                    byte[] bs = new byte[4096];

                    int value;
                    while ((value = zipInputStream.read(bs)) > 0) {
                      fileOutputStream.write(bs, 0, value);
                    }
                  } finally {
                    if (fileOutputStream != null) {
                      fileOutputStream.close();
                    }
                  }
                } catch (Throwable throwable3) {
                  if (primaryError == null) {
                    primaryError = throwable3;
                  } else if (primaryError != throwable3) {
                    primaryError.addSuppressed(throwable3);
                  }

                  throw sneakyThrow(primaryError);
                }

                zipInputStream.closeEntry();
                CMIMessages.sendMessage(commandSender, "&fUnzipped: &7" + targetFile.getName());
              }
            } finally {
              if (zipInputStream != null) {
                zipInputStream.close();
              }
            }
          } catch (Throwable throwable4) {
            if (throwable == null) {
              throwable = throwable4;
            } else if (throwable != throwable4) {
              throwable.addSuppressed(throwable4);
            }

            if (fileInputStream != null) {
              fileInputStream.close();
            }

            throw CMIBackUpManager.<IOException>sneakyThrow(throwable);
          }

          if (fileInputStream != null) {
            fileInputStream.close();
          }
        } catch (Throwable throwable5) {
          if (throwable == null) {
            throwable = throwable5;
          } else if (throwable != throwable5) {
            throwable.addSuppressed(throwable5);
          }

          throw CMIBackUpManager.<IOException>sneakyThrow(throwable);
        }
      } catch (IOException ioException) {
        ioException.printStackTrace();
        unzipping = false;
        return null;
      }

      unzipping = false;
      return targetFile;
    } else {
      CMIMessages.sendMessage(
          commandSender, "Backup file does not exist: &7" + file.getAbsolutePath());
      return null;
    }
  }

  private static CompletableFuture<Map<DBDAO.DBTables, Boolean>> copyTables(
      Connection connection, Connection otherConnection, DBDAO.DBTables... dbTableses) {
    return CompletableFuture.supplyAsync(
        () -> {
          CMIMessages.consoleMessage("[CMI] Start Back up");
          HashMap<com.Zrips.CMI.Modules.DataBase.DBDAO.DBTables, Boolean> hashMap = new HashMap();
          var arrayList = new ArrayList<>(Arrays.asList(dbTableses));
          if (!arrayList.contains(DBDAO.DBTables.UserTable)) {
            arrayList.add(DBDAO.DBTables.UserTable);
          }

          for (DBDAO.DBTables dbTables : arrayList) {
            boolean flag = copyUserTable(dbTables, connection, otherConnection);
            hashMap.put(dbTables, flag);
            if (flag) {
              CMIMessages.consoleMessage("[CMI] Copied " + dbTables.getTableNameOnly() + " data");
            } else {
              CMIMessages.consoleMessage(
                  "[CMI] Failed to copy " + dbTables.getTableNameOnly() + " data");
            }
          }

          CMIMessages.consoleMessage("[CMI] Finish Back up copy process");
          return hashMap;
        },
        CMIThread.getExecutor());
  }

  // $VF: Could not inline inconsistent finally blocks
  // Please report this to the Vineflower issue tracker, at
  // https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the
  // rights to distribute it!)
  private static boolean copyUserTable(
      DBDAO.DBTables dbTables, Connection connection, Connection targetConnection) {
    String string = dbTables.getTableName();
    String tableName = dbTables.getTableNameOnly();
    boolean flag = false;

    try {
      Throwable throwable = null;
      Object object = null;

      try {
        Statement statement = connection.createStatement();

        try {
          ResultSet resultSet = statement.executeQuery("SELECT * FROM `" + string + "`");

          try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int value = resultSetMetaData.getColumnCount();
            ArrayList<String> arrayList = new ArrayList();
            String primaryKey = dbTables.hasColumn("player_id") ? "player_id" : "id";

            for (int columnIndex = 1; columnIndex <= value; columnIndex++) {
              String columnName = resultSetMetaData.getColumnName(columnIndex);
              if (dbTables.hasColumn(columnName) || columnName.equals(primaryKey)) {
                arrayList.add(columnName);
              }
            }

            DatabaseMetaData databaseMetaData = targetConnection.getMetaData();
            String productName = databaseMetaData.getDatabaseProductName();
            StringBuilder stringBuilder = new StringBuilder();
            if ("MySQL".equalsIgnoreCase(productName)) {
              stringBuilder
                  .append("INSERT INTO ")
                  .append(tableName)
                  .append(" (")
                  .append(String.join(",", arrayList))
                  .append(") VALUES (");

              for (int rowIndex = 0; rowIndex < arrayList.size(); rowIndex++) {
                stringBuilder.append("?");
                if (rowIndex < arrayList.size() - 1) {
                  stringBuilder.append(",");
                }
              }

              stringBuilder.append(") ON DUPLICATE KEY UPDATE ");

              for (int nameIndex = 0; nameIndex < arrayList.size(); nameIndex++) {
                String columnToCopy = (String) arrayList.get(nameIndex);
                if (!columnToCopy.equals(primaryKey)) {
                  stringBuilder
                      .append(columnToCopy)
                      .append("=VALUES(")
                      .append(columnToCopy)
                      .append(")");
                  if (nameIndex < arrayList.size() - 1) {
                    stringBuilder.append(",");
                  }
                }
              }
            } else {
              if (!"SQLite".equalsIgnoreCase(productName)) {
                throw new SQLException("Unsupported database type: " + productName);
              }

              stringBuilder
                  .append("INSERT OR REPLACE INTO ")
                  .append(tableName)
                  .append(" (")
                  .append(String.join(",", arrayList))
                  .append(") VALUES (");

              for (int pkIndex = 0; pkIndex < arrayList.size(); pkIndex++) {
                stringBuilder.append("?");
                if (pkIndex < arrayList.size() - 1) {
                  stringBuilder.append(",");
                }
              }

              stringBuilder.append(")");
            }

            Throwable primaryError = null;
            Object resourceRef = null;

            try {
              PreparedStatement preparedStatement =
                  targetConnection.prepareStatement(stringBuilder.toString());

              try {
                targetConnection.setAutoCommit(false);
                short shortValue = 1000;
                int count = 0;

                while (resultSet.next()) {
                  for (int valueIndex = 0; valueIndex < arrayList.size(); valueIndex++) {
                    preparedStatement.setObject(
                        valueIndex + 1, resultSet.getObject((String) arrayList.get(valueIndex)));
                  }

                  preparedStatement.addBatch();
                  if (++count % shortValue == 0) {
                    preparedStatement.executeBatch();
                  }
                }

                preparedStatement.executeBatch();
                targetConnection.commit();
                flag = true;
              } finally {
                if (preparedStatement != null) {
                  preparedStatement.close();
                }
              }
            } catch (Throwable throwable3) {
              if (primaryError == null) {
                primaryError = throwable3;
              } else if (primaryError != throwable3) {
                primaryError.addSuppressed(throwable3);
              }

              throw sneakyThrow(primaryError);
            }
          } finally {
            if (resultSet != null) {
              resultSet.close();
            }
          }
        } catch (Throwable throwable4) {
          if (throwable == null) {
            throwable = throwable4;
          } else if (throwable != throwable4) {
            throwable.addSuppressed(throwable4);
          }

          if (statement != null) {
            statement.close();
          }

          throw CMIBackUpManager.<SQLException>sneakyThrow(throwable);
        }

        if (statement != null) {
          statement.close();
        }
      } catch (Throwable throwable5) {
        if (throwable == null) {
          throwable = throwable5;
        } else if (throwable != throwable5) {
          throwable.addSuppressed(throwable5);
        }

        throw CMIBackUpManager.<SQLException>sneakyThrow(throwable);
      }
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();

      try {
        targetConnection.rollback();
      } catch (SQLException sqlException2) {
        sqlException2.printStackTrace();
      }
    }

    return flag;
  }

  public static CompletableFuture<File> getFileAsync(String string) {
    return CompletableFuture.supplyAsync(
        () -> {
          File file = new File(CMI.getInstance().getDataFolder(), databaseBackups);
          if (!file.isDirectory()) {
            return null;
          }

          File backupFile =
              Arrays.stream(
                      file.listFiles(
                          (directory, fileName) ->
                              fileName.endsWith(".db")
                                  && (fileName.equals(string) || fileName.equals(string + ".db"))))
                  .findFirst()
                  .orElse(null);
          return backupFile != null
              ? backupFile
              : Arrays.stream(
                      file.listFiles(
                          (directory, zipFileName) ->
                              zipFileName.endsWith(".zip")
                                  && (zipFileName.equals(string)
                                      || zipFileName.equals(string + ".zip"))))
                  .findFirst()
                  .orElse(null);
        },
        CMIThread.getExecutor());
  }

  public static void cleanOldBackups() {
    File file = new File(CMI.getInstance().getDataFolder(), databaseBackups);
    if (file.isDirectory()) {
      File[] files =
          file.listFiles(
              (directory, string) ->
                  string.endsWith(defaultSuffix) || string.endsWith(defaultSuffix + ".zip"));
      if (files != null && files.length > maxBackups) {
        HashMap<String, List<File>> hashMap = new HashMap<>();
        File[] fileList = files;
        int value = files.length;

        for (int fileIndex = 0; fileIndex < value; fileIndex++) {
          File currentFile = fileList[fileIndex];
          String key =
              currentFile.getName().endsWith(".zip")
                  ? currentFile.getName().substring(0, currentFile.getName().length() - 4)
                  : currentFile.getName();
          hashMap.computeIfAbsent(key, object -> new ArrayList<>()).add(currentFile);
        }

        ArrayList<List<File>> arrayList = new ArrayList<>(hashMap.values());
        arrayList.sort(
            Comparator.comparingLong(
                group -> group.stream().mapToLong(File::lastModified).max().orElse(0L)));
        int excess = arrayList.size() - maxBackups;

        for (int removeIndex = 0; removeIndex < excess; removeIndex++) {
          for (File oldFile : (List<File>) arrayList.get(removeIndex)) {
            if (!oldFile.delete()) {
              CMIMessages.consoleMessage("[CMI] Failed to delete: " + oldFile.getAbsolutePath());
            } else {
              CMIMessages.consoleMessage("[CMI] Deleted old backup: " + oldFile.getName());
            }
          }
        }
      }
    }
  }

  public static CompletableFuture<CMIBackupFeedback> backupDataBase() {
    return backupDataBase(manualTablesToSave.toArray(new DBDAO.DBTables[0]));
  }

  public static CompletableFuture<CMIBackupFeedback> backupDataBase(
      @Nonnull DBDAO.DBTables... dbTableses) {
    return CompletableFuture.<File>supplyAsync(
            () -> {
              File file = new File(CMI.getInstance().getDataFolder(), databaseBackups);
              return new File(
                  file,
                  new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_").format(new Date()) + defaultSuffix);
            },
            CMIThread.getExecutor())
        .thenCompose(backupFile -> backupDataBase(backupFile, dbTableses));
  }

  public static CompletableFuture<CMIBackupFeedback> backupDataBase(
      @Nonnull String string, @Nonnull DBDAO.DBTables... dbTableses) {
    return CompletableFuture.<File>supplyAsync(
            () -> {
              String dbName = string.endsWith(".db") ? string : string + ".db";
              File file = new File(CMI.getInstance().getDataFolder(), databaseBackups);
              return new File(file, dbName);
            },
            CMIThread.getExecutor())
        .thenCompose(backupFile -> backupDataBase(backupFile, dbTableses));
  }

  public static CompletableFuture<CMIBackupFeedback> backupDataBase(
      @Nonnull File file, @Nonnull DBDAO.DBTables... dbTableses) {
    return CompletableFuture.<DBSQLite>supplyAsync(
            () -> {
              verifyFolder();
              DBSQLite dbsqLite = new DBSQLite(CMI.getInstance(), file);
              dbsqLite.setUpTables();
              return dbsqLite;
            },
            CMIThread.getExecutor())
        .thenCompose(
            dbFile -> {
              Connection connection =
                  CMI.getInstance().getDbManager().getDB().getTemporaryConnection();
              return copyTables(connection, dbFile.getConnection().getConnection(), dbTableses)
                  .thenCompose(
                      map ->
                          CompletableFuture.<File>supplyAsync(
                                  () -> {
                                    File resultFile = null;
                                    if (zip) {
                                      resultFile = zipFile(file);
                                    }

                                    return resultFile;
                                  },
                                  CMIThread.getExecutor())
                              .thenApply(
                                  completedFile -> {
                                    try {
                                      connection.close();
                                    } catch (SQLException sqlException) {
                                      sqlException.printStackTrace();
                                    }

                                    try {

                                      dbFile.closeConnections();

                                    } catch (SQLException sqlException) {

                                      sqlException.printStackTrace();
                                    }
                                    if (zip && completedFile != null) {
                                      if (file.delete()) {
                                        CMIMessages.consoleMessage("[CMI] Removed temporary file");
                                      } else {
                                        CMIMessages.consoleMessage(
                                            "[CMI] Failed to remove temporary file");
                                      }
                                    }

                                    cleanOldBackups();
                                    return new CMIBackupFeedback(
                                        file, (Map<DBDAO.DBTables, Boolean>) map);
                                  }));
            });
  }

  public static CompletableFuture<CMIBackupFeedback> restoreUserFromBackup(
      CommandSender commandSender,
      @Nonnull CMIUser cmiUser,
      @Nonnull DBDAO.DBTables... dbTableses) {
    return restoreUserData(commandSender, cmiUser, dbTableses)
        .whenComplete(
            (cmiBackupFeedback, throwable) -> {
              if (throwable != null) {
                throwable.printStackTrace();
              } else {
                finalizeUserRestore(cmiUser, cmiBackupFeedback);
              }
            });
  }

  public static CompletableFuture<CMIBackupFeedback> restoreUserFromBackup(
      CommandSender commandSender,
      @Nonnull CMIUser cmiUser,
      @Nonnull File file,
      @Nonnull DBDAO.DBTables... dbTableses) {
    return restoreUserData(commandSender, cmiUser, file, dbTableses)
        .whenComplete(
            (cmiBackupFeedback, throwable) -> {
              if (throwable != null) {
                throwable.printStackTrace();
              } else {
                finalizeUserRestore(cmiUser, cmiBackupFeedback);
              }
            });
  }

  private static void finalizeUserRestore(
      @Nonnull CMIUser cmiUser, CMIBackupFeedback cmiBackupFeedback) {
    if (cmiBackupFeedback.getTables().containsKey(DBDAO.DBTables.UserTable)) {
      CMI.getInstance()
          .getDbManager()
          .loadUserAsync(cmiUser.getUniqueId())
          .thenAccept(ignored -> updateSecondary(cmiUser, cmiBackupFeedback.getTables()));
    } else {
      updateSecondary(cmiUser, cmiBackupFeedback.getTables());
    }
  }

  private static void updateSecondary(
      @Nonnull CMIUser cmiUser, @Nonnull Map<DBDAO.DBTables, Boolean> map) {
    if (map.getOrDefault(DBDAO.DBTables.PlayTimeReward, false)) {
      CMI.getInstance().getDbManager().getDB().loadPlayerPlayTimeRewards(cmiUser);
    }

    if (map.getOrDefault(DBDAO.DBTables.InvTable, false)) {
      CMI.getInstance().getSavedInventoryManager().removeCacheInventory(cmiUser.getUniqueId());
    }

    if (map.getOrDefault(DBDAO.DBTables.PlayTime, false)) {
      CMI.getInstance().getDbManager().getDB().loadPlayTimes(cmiUser);
    }

    CMI.getInstance().getDbManager().recheckIDs(cmiUser);
  }

  public static File getLatestBackup() {
    File file = new File(CMI.getInstance().getDataFolder(), databaseBackups);
    if (!file.isDirectory()) {
      return null;
    }

    File[] files =
        file.listFiles(
            (directory, string) ->
                string.endsWith(defaultSuffix) || string.endsWith(defaultSuffix + ".zip"));
    if (files != null && files.length != 0) {
      HashMap<String, File> hashMap = new HashMap();
      File[] fileArray = files;
      int value = files.length;

      for (int fileIndex = 0; fileIndex < value; fileIndex++) {
        File candidate = fileArray[fileIndex];
        String fileKey =
            candidate.getName().endsWith(".zip")
                ? candidate.getName().substring(0, candidate.getName().length() - 4)
                : candidate.getName();
        File mappedFile = (File) hashMap.get(fileKey);
        if (mappedFile == null) {
          hashMap.put(fileKey, candidate);
        } else if (candidate.lastModified() > mappedFile.lastModified()
            || candidate.getName().endsWith(defaultSuffix)
                && mappedFile.getName().endsWith(".zip")) {
          hashMap.put(fileKey, candidate);
        }
      }

      return hashMap.values().stream()
          .max(Comparator.comparingLong(File::lastModified))
          .orElse(null);
    } else {
      return null;
    }
  }

  @Nonnull
  private static CompletableFuture<CMIBackupFeedback> restoreUserData(
      CommandSender commandSender,
      @Nonnull CMIUser cmiUser,
      @Nonnull DBDAO.DBTables... dbTableses) {
    return CompletableFuture.supplyAsync(CMIBackUpManager::getLatestBackup, CMIThread.getExecutor())
        .thenCompose(
            file -> {
              if (file == null) {
                CMIMessages.sendMessage(
                    commandSender, "Could not automatically determine last backup file");
                return CompletableFuture.completedFuture(null);
              } else {
                CMIMessages.sendMessage(commandSender, "Restoring from: &7" + file.getName());
                return restoreUserData(commandSender, cmiUser, file, dbTableses);
              }
            });
  }

  private static CompletableFuture<CMIBackupFeedback> restoreUserData(
      CommandSender commandSender,
      @Nonnull CMIUser cmiUser,
      @Nonnull File file,
      @Nonnull DBDAO.DBTables... dbTableses) {
    return CompletableFuture.supplyAsync(
        () -> {
          if (unzipping) {
            CMIMessages.sendMessage(
                commandSender, "File is already being unzipped, try again later");
            return new CMIBackupFeedback(file, Collections.emptyMap());
          }

          File sourceFile = file;
          if (file.getName().toLowerCase().endsWith(".zip")) {
            CMIMessages.sendMessage(commandSender, "Unzipping file: &7" + file.getName());
            sourceFile = unzipFile(commandSender, file);
          }

          if (!sourceFile.getName().toLowerCase().endsWith(".db")) {
            CMIMessages.sendMessage(commandSender, "Invalid backup file: &7" + file.getName());
            return new CMIBackupFeedback(file, Collections.emptyMap());
          }

          Connection connection = CMI.getInstance().getDbManager().getDB().getTemporaryConnection();
          DBSQLite dbsqLite = new DBSQLite(CMI.getInstance(), sourceFile);
          HashMap<com.Zrips.CMI.Modules.DataBase.DBDAO.DBTables, Boolean> hashMap = new HashMap();
          DBDAO.DBTables[] tables = dbTableses;
          int value = dbTableses.length;

          for (int tableIndex = 0; tableIndex < value; tableIndex++) {
            DBDAO.DBTables dbTables = tables[tableIndex];
            boolean flag =
                copyUserRow(
                    dbTables,
                    dbsqLite.getConnection().getConnection(),
                    connection,
                    cmiUser.getUniqueId(),
                    cmiUser.getId());
            hashMap.put(dbTables, flag);
            if (flag) {
              CMIMessages.sendMessage(
                  commandSender, "Restored &7" + dbTables.getTableName() + " &ftable entry");
            } else {
              CMIMessages.sendMessage(
                  commandSender,
                  "Skipped &7"
                      + dbTables.getTableName()
                      + " &ftable entry. Missing backup record?");
            }
          }

          try {
            connection.close();
          } catch (SQLException sqlException) {
            sqlException.printStackTrace();
          }

          try {

            dbsqLite.closeConnections();

          } catch (SQLException sqlException) {

            sqlException.printStackTrace();
          }
          return new CMIBackupFeedback(sourceFile, hashMap);
        },
        CMIThread.getExecutor());
  }

  // $VF: Could not inline inconsistent finally blocks
  // Please report this to the Vineflower issue tracker, at
  // https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the
  // rights to distribute it!)
  private static boolean copyUserRow(
      DBDAO.DBTables dbTables,
      Connection connection,
      Connection copyConnection,
      UUID uuid,
      int value) {
    String string = dbTables.getTableNameOnly();
    String tableName = dbTables.getTableName();
    boolean flag = false;
    boolean hasPlayerId = dbTables.hasColumn("player_id");
    String keyColumn = hasPlayerId ? "player_id" : "id";
    int rowCount = 0;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      preparedStatement =
          connection.prepareStatement(
              "SELECT * FROM `"
                  + DBDAO.DBTables.UserTable.getTableNameOnly()
                  + "` WHERE `player_uuid` = ?;");
      preparedStatement.setString(1, uuid.toString());
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        rowCount = resultSet.getInt("id");
      }
    } catch (Error | Exception value3) {
      value3.printStackTrace();
    } finally {
      close(resultSet);
      close(preparedStatement);
    }

    if (rowCount == 0) {
      return false;
    }

    try {
      Throwable throwable = null;
      Object object = null;

      try {
        PreparedStatement selectStatement =
            connection.prepareStatement(
                "SELECT * FROM `" + string + "` WHERE `" + keyColumn + "` = ? LIMIT 1");

        try {
          selectStatement.setInt(1, rowCount);
          Throwable primaryError = null;
          Object resourceRef = null;

          try {
            ResultSet selectResult = selectStatement.executeQuery();

            try {
              if (selectResult.next()) {
                ResultSetMetaData resultSetMetaData = selectResult.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();
                ArrayList<String> arrayList = new ArrayList();

                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                  String columnName = resultSetMetaData.getColumnName(columnIndex);
                  if (dbTables.hasColumn(columnName)) {
                    arrayList.add(columnName);
                  }
                }

                StringBuilder stringBuilder =
                    new StringBuilder("UPDATE `").append(tableName).append("` SET ");
                ArrayList<String> columnNames = new ArrayList();

                for (String column : arrayList) {
                  if (!column.equals(keyColumn)) {
                    columnNames.add(column);
                  }
                }

                for (int columnNameIndex = 0;
                    columnNameIndex < columnNames.size();
                    columnNameIndex++) {
                  if (columnNameIndex > 0) {
                    stringBuilder.append(",");
                  }

                  stringBuilder
                      .append("`")
                      .append((String) columnNames.get(columnNameIndex))
                      .append("`=?");
                }

                stringBuilder.append(" WHERE `").append(keyColumn).append("`=?");
                copyConnection.setAutoCommit(false);
                Throwable secondaryError = null;
                Object placeholders = null;

                int placeholderIndex;
                try {
                  PreparedStatement insertStatement =
                      copyConnection.prepareStatement(stringBuilder.toString());

                  try {
                    int insertIndex = 1;

                    for (String columnItem : columnNames) {
                      insertStatement.setObject(insertIndex++, selectResult.getObject(columnItem));
                    }

                    insertStatement.setInt(insertIndex, value);
                    placeholderIndex = insertStatement.executeUpdate();
                  } finally {
                    if (insertStatement != null) {
                      insertStatement.close();
                    }
                  }
                } catch (Throwable throwable4) {
                  if (secondaryError == null) {
                    secondaryError = throwable4;
                  } else if (secondaryError != throwable4) {
                    secondaryError.addSuppressed(throwable4);
                  }

                  throw sneakyThrow(secondaryError);
                }

                if (placeholderIndex == 0) {
                  String quotedColumns =
                      arrayList.stream()
                          .map(columnValue -> "`" + columnValue + "`")
                          .collect(Collectors.joining(","));
                  placeholders =
                      arrayList.stream().map(unusedValue -> "?").collect(Collectors.joining(","));
                  String insertSql =
                      "INSERT INTO `"
                          + tableName
                          + "` ("
                          + quotedColumns
                          + ") VALUES ("
                          + placeholders
                          + ")";
                  Throwable tertiaryError = null;
                  Object lastResource = null;

                  try {
                    PreparedStatement insertBatch = copyConnection.prepareStatement(insertSql);

                    try {
                      for (int valueIndex = 0; valueIndex < arrayList.size(); valueIndex++) {
                        insertBatch.setObject(
                            valueIndex + 1,
                            selectResult.getObject((String) arrayList.get(valueIndex)));
                      }

                      insertBatch.executeUpdate();
                    } finally {
                      if (insertBatch != null) {
                        insertBatch.close();
                      }
                    }
                  } catch (Throwable throwable6) {
                    if (tertiaryError == null) {
                      tertiaryError = throwable6;
                    } else if (tertiaryError != throwable6) {
                      tertiaryError.addSuppressed(throwable6);
                    }

                    throw sneakyThrow(tertiaryError);
                  }
                }

                copyConnection.commit();
                flag = true;
                return flag;
              }
            } finally {
              if (selectResult != null) {
                selectResult.close();
              }
            }
          } catch (Throwable throwable7) {
            if (primaryError == null) {
              primaryError = throwable7;
            } else if (primaryError != throwable7) {
              primaryError.addSuppressed(throwable7);
            }

            throw sneakyThrow(primaryError);
          }
        } finally {
          if (selectStatement != null) {
            selectStatement.close();
          }
        }

        return false;
      } catch (Throwable throwable8) {
        if (throwable == null) {
          throwable = throwable8;
        } else if (throwable != throwable8) {
          throwable.addSuppressed(throwable8);
        }

        throw CMIBackUpManager.<SQLException>sneakyThrow(throwable);
      }
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();

      try {
        copyConnection.rollback();
      } catch (SQLException sqlException2) {
        sqlException2.printStackTrace();
      }
    }

    return flag;
  }

  private static void close(ResultSet resultSet) {
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (Error | Exception value) {
        value.printStackTrace();
      }
    }
  }

  private static void close(Statement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (Error | Exception value) {
        value.printStackTrace();
      }
    }
  }

  /**
   * Rethrows the object unchanged (the original bytecode throws Throwable without a declaration).
   */
  @SuppressWarnings("unchecked")
  private static <T extends Throwable> RuntimeException sneakyThrow(Throwable throwable) throws T {
    throw (T) throwable;
  }
}
