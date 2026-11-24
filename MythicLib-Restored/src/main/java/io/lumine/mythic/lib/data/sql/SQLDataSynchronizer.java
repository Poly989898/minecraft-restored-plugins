package io.lumine.mythic.lib.data.sql;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public abstract class SQLDataSynchronizer<H extends SynchronizedDataHolder> {
   private final SQLDataSource dataSource;
   private final H playerData;
   private final UUID effectiveId;
   private final String tableName;
   private final String uuidFieldName;
   private final long start = System.currentTimeMillis();
   private int tries;
   private static final int RETRIEVAL_PERIOD = 1000;

   @Deprecated
   public SQLDataSynchronizer(String tableName, String uuidFieldName, SQLDataSource dataSource, H playerData, boolean profilePlugin) {
      this(tableName, uuidFieldName, dataSource, playerData);
   }

   public SQLDataSynchronizer(String tableName, String uuidFieldName, SQLDataSource dataSource, H playerData) {
      this.tableName = tableName;
      this.uuidFieldName = uuidFieldName;
      this.playerData = playerData;
      this.dataSource = dataSource;
      this.effectiveId = playerData.getEffectiveId();
   }

   public H getData() {
      return this.playerData;
   }

   public boolean synchronize() {
      this.tries++;
      Connection connection = null;
      PreparedStatement prepared = null;
      ResultSet result = null;
      boolean retry = false;
      boolean success = false;

      try {
         connection = this.dataSource.getConnection();
         prepared = connection.prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `" + this.uuidFieldName + "` = ?;");
         prepared.setString(1, this.effectiveId.toString());
         UtilityMethods.debug(this.dataSource.getPlugin(), "SQL", "Trying to load data of " + this.effectiveId);
         result = prepared.executeQuery();
         if (this.playerWentOffline()) {
            UtilityMethods.debug(this.dataSource.getPlugin(), "SQL", "Stopped data retrieval as '" + this.effectiveId + "' went offline");
            return false;
         }

         if (result.next()) {
            if (this.tries <= MythicLib.plugin.getMMOConfig().maxSyncTries && result.getInt("is_saved") != 1) {
               UtilityMethods.debug(
                  this.dataSource.getPlugin(),
                  "SQL",
                  "Did not load data of '" + this.effectiveId + "' as 'is_saved' is set to 0, trying again in " + 1000 + "ms"
               );
               retry = true;
            } else {
               this.confirmReception(connection);
               success = true;
               this.loadData(result);
               if (this.tries > MythicLib.plugin.getMMOConfig().maxSyncTries) {
                  UtilityMethods.debug(this.dataSource.getPlugin(), "SQL", "Maximum number of tries reached.");
               }

               UtilityMethods.debug(this.dataSource.getPlugin(), "SQL", "Found and loaded data of '" + this.effectiveId + "'");
               UtilityMethods.debug(this.dataSource.getPlugin(), "SQL", "Time taken: " + (System.currentTimeMillis() - this.start) + "ms");
            }
         } else {
            this.confirmReception(connection);
            success = true;
            this.loadEmptyData();
            UtilityMethods.debug(this.dataSource.getPlugin(), "SQL", "Found empty data for '" + this.effectiveId + "', loading default...");
         }
      } catch (Exception throwable) {
         this.dataSource.getPlugin().getLogger().log(Level.WARNING, "Could not load player data of '" + this.effectiveId + "':");
         throwable.printStackTrace();
      } finally {
         try {
            if (result != null) {
               result.close();
            }

            if (prepared != null) {
               prepared.close();
            }

            if (connection != null) {
               connection.close();
            }
         } catch (SQLException exception) {
            this.dataSource.getPlugin().getLogger().log(Level.WARNING, "Could not load player data of '" + this.effectiveId + "':");
            exception.printStackTrace();
         }
      }

      if (retry) {
         try {
            Thread.sleep(1000L);
         } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
         }

         return this.synchronize();
      } else {
         return success;
      }
   }

   private boolean playerWentOffline() {
      return !this.playerData.getMMOPlayerData().isLookup() && !this.playerData.getMMOPlayerData().isOnline();
   }

   private void confirmReception(Connection connection) throws SQLException {
      if (!this.playerData.getMMOPlayerData().isLookup()) {
         PreparedStatement prepared = null;

         try {
            prepared = connection.prepareStatement(
               "INSERT INTO " + this.tableName + "(`uuid`, `is_saved`) VALUES(?, 0) ON DUPLICATE KEY UPDATE `is_saved` = 0;"
            );
            prepared.setString(1, this.effectiveId.toString());
            prepared.executeUpdate();
         } catch (Exception exception) {
            this.dataSource.getPlugin().getLogger().log(Level.WARNING, "Could not confirm data sync of " + this.effectiveId);
            exception.printStackTrace();
         } finally {
            if (prepared != null) {
               prepared.close();
            }
         }
      }
   }

   public abstract void loadData(ResultSet var1) throws SQLException, IOException, ClassNotFoundException;

   public abstract void loadEmptyData();
}
